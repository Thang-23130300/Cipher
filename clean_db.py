import re
import subprocess
import os

input_file = r"D:\MonHoc\ATHTTT\DB\souvenirdb.sql"
output_file = r"D:\MonHoc\ATHTTT\DB\souvenirdb_clean.sql"

# 1. Fetch current tables and columns from database 'cipher'
print("Fetching columns from database 'cipher'...")
db_columns = {}
cmd = [
    r"C:\xampp\mysql\bin\mysql.exe",
    "-u", "root",
    "-e", "SELECT table_name, column_name FROM information_schema.columns WHERE table_schema = 'cipher'",
    "--batch", "--silent"
]
result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
if result.returncode != 0:
    print("Error getting database columns:", result.stderr)
    exit(1)

for line in result.stdout.strip().split("\n"):
    if not line:
        continue
    parts = line.split("\t")
    if len(parts) == 2:
        tbl, col = parts
        if tbl not in db_columns:
            db_columns[tbl] = set()
        db_columns[tbl].add(col)

print(f"Loaded {len(db_columns)} tables from database schema.")

# 2. Function to split values in INSERT statement
def split_values(row_str):
    row_str = row_str.strip()
    if row_str.endswith(";"):
        row_str = row_str[:-1]
    
    start = row_str.find('(')
    end = row_str.rfind(')')
    if start != -1 and end != -1:
        row_str = row_str[start+1:end]
        
    values = []
    current = []
    in_quotes = False
    quote_char = None
    escaped = False
    
    i = 0
    n = len(row_str)
    while i < n:
        char = row_str[i]
        if escaped:
            current.append(char)
            escaped = False
        elif char == '\\':
            current.append(char)
            escaped = True
        elif char in ("'", '"'):
            if in_quotes:
                if char == quote_char:
                    if i + 1 < n and row_str[i+1] == quote_char:
                        current.append(char)
                        current.append(char)
                        i += 1
                    else:
                        in_quotes = False
                        quote_char = None
                        current.append(char)
                else:
                    current.append(char)
            else:
                in_quotes = True
                quote_char = char
                current.append(char)
        elif char == ',' and not in_quotes:
            values.append("".join(current).strip())
            current = []
        else:
            current.append(char)
        i += 1
        
    values.append("".join(current).strip())
    return values

# 3. Read and parse souvenirdb.sql
table_columns = {}
current_table = None
in_create_table = False
create_table_lines = []

output_lines = [
    "SET NAMES utf8mb4;\n",
    "SET FOREIGN_KEY_CHECKS = 0;\n",
    "use cipher;\n\n"
]

print("Starting to parse SQL file and clean it...")
written_records = {}

with open(input_file, "r", encoding="utf-8", errors="ignore") as f:
    for line_num, line in enumerate(f, 1):
        line_stripped = line.strip()
        
        # Detect CREATE TABLE
        create_match = re.match(r"CREATE TABLE `([^`]+)`", line_stripped, re.IGNORECASE)
        if create_match:
            current_table = create_match.group(1)
            table_columns[current_table] = []
            in_create_table = True
            create_table_lines = [line]
            continue
        
        if in_create_table:
            create_table_lines.append(line)
            if line_stripped.startswith(")") or line_stripped.startswith("PRIMARY KEY") or line_stripped.startswith("KEY") or line_stripped.startswith("CONSTRAINT") or line_stripped.startswith("UNIQUE INDEX") or line_stripped.startswith("INDEX"):
                if line_stripped.startswith(")"):
                    in_create_table = False
                    # If the table is missing in database, write CREATE TABLE block
                    if current_table not in db_columns:
                        print(f"Table '{current_table}' is missing from database. Will create it during import.")
                        output_lines.extend(create_table_lines)
                        output_lines.append("\n")
                continue
            
            col_match = re.match(r"\s*`([^`]+)`", line)
            if col_match and current_table:
                table_columns[current_table].append(col_match.group(1))
            continue
        
        # Detect INSERT INTO
        # Format: INSERT INTO `table` VALUES ( ... );
        insert_match = re.match(r"INSERT INTO `([^`]+)`\s+VALUES\s*(.*)", line_stripped, re.IGNORECASE)
        if insert_match:
            table_name = insert_match.group(1)
            values_part = insert_match.group(2)
            
            # Clean invalid dates in the raw values string
            values_part = values_part.replace("'0000-00-00 00:00:00'", "'2026-06-11 00:00:00'")
            values_part = values_part.replace("'0000-00-00 00:00:00.000000'", "'2026-06-11 00:00:00.000000'")
            
            parsed_vals = split_values(values_part)
            original_cols = table_columns.get(table_name, [])
            
            if len(parsed_vals) != len(original_cols):
                print(f"Warning line {line_num}: Column count mismatch for {table_name}. Original cols: {len(original_cols)}, values: {len(parsed_vals)}. Skipping.")
                continue
            
            if table_name in db_columns:
                # Table exists in database, filter columns
                filtered_cols = []
                filtered_vals = []
                for col, val in zip(original_cols, parsed_vals):
                    if col in db_columns[table_name]:
                        filtered_cols.append(col)
                        filtered_vals.append(val)
                
                if filtered_cols:
                    cols_str = ", ".join(f"`{c}`" for c in filtered_cols)
                    vals_str = ", ".join(filtered_vals)
                    
                    # Clear existing rows on first insert
                    if table_name not in written_records:
                        output_lines.append(f"DELETE FROM `{table_name}`;\n")
                        
                    new_insert = f"INSERT INTO `{table_name}` ({cols_str}) VALUES ({vals_str});\n"
                    output_lines.append(new_insert)
                    written_records[table_name] = written_records.get(table_name, 0) + 1
            else:
                # Table does not exist in database, write as is (with all columns)
                cols_str = ", ".join(f"`{c}`" for c in original_cols)
                vals_str = ", ".join(parsed_vals)
                
                if table_name not in written_records:
                    output_lines.append(f"DELETE FROM `{table_name}`;\n")
                    
                new_insert = f"INSERT INTO `{table_name}` ({cols_str}) VALUES ({vals_str});\n"
                output_lines.append(new_insert)
                written_records[table_name] = written_records.get(table_name, 0) + 1

# Append enabling foreign key checks at the end
output_lines.append("\nSET FOREIGN_KEY_CHECKS = 1;\n")

# Make sure output directory exists
os.makedirs(os.path.dirname(output_file), exist_ok=True)

with open(output_file, "w", encoding="utf-8") as out_f:
    out_f.writelines(output_lines)

print(f"\nSuccessfully generated clean SQL file at: {output_file}")
print("Written records summary:")
for t, count in sorted(written_records.items()):
    print(f"- {t}: {count} records")
