package nlu.fit.web.souvenirecommerce.features.user.profile.service;

import nlu.fit.web.souvenirecommerce.features.user.profile.repository.AddressRepository;
import nlu.fit.web.souvenirecommerce.features.user.profile.repository.ProfileRepository;
import nlu.fit.web.souvenirecommerce.model.entity.Address;
import nlu.fit.web.souvenirecommerce.model.entity.Province;
import nlu.fit.web.souvenirecommerce.model.entity.User;
import nlu.fit.web.souvenirecommerce.model.entity.Ward;

import java.util.List;
import java.util.Optional;

public class AddressService {
    private final AddressRepository addressRepository = new AddressRepository();
    private final ProfileRepository profileRepository = new ProfileRepository();

    public List<Address> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    public List<Province> getProvinces() {
        return profileRepository.findAllProvinces();
    }

    public List<Ward> getWardsByProvinceCode(Integer provinceCode) {
        return profileRepository.findWardsByProvinceCode(provinceCode);
    }

    public boolean addAddress(User user, String addressDetail, Integer provinceCode, Integer wardCode) {
        if (user == null || user.getId() == null || isBlank(addressDetail)) {
            return false;
        }

        Optional<Province> province = profileRepository.findProvinceByCode(provinceCode);
        Optional<Ward> ward = profileRepository.findWardByCode(wardCode);
        if (province.isEmpty() || ward.isEmpty()) {
            return false;
        }

        Province selectedProvince = province.get();
        Ward selectedWard = ward.get();
        if (selectedWard.getProvince() == null || !selectedProvince.getCode().equals(selectedWard.getProvince().getCode())) {
            return false;
        }

        Address address = Address.builder()
                .user(user)
                .receiverName(user.getFullName())
                .receiverPhone(user.getPhone())
                .addressDetail(addressDetail.trim())
                .province(displayName(selectedProvince.getFullName(), selectedProvince.getName()))
                .district("")
                .ward(displayName(selectedWard.getFullName(), selectedWard.getName()))
                .provinceEntity(selectedProvince)
                .wardEntity(selectedWard)
                .isDefault(addressRepository.countByUserId(user.getId()) == 0)
                .build();
        return addressRepository.save(address).isPresent();
    }

    public boolean setDefaultAddress(Long userId, Long addressId) {
        if (userId == null || addressId == null) {
            return false;
        }
        return addressRepository.setDefault(addressId, userId);
    }

    public boolean deleteAddress(Long userId, Long addressId) {
        if (userId == null || addressId == null) {
            return false;
        }
        return addressRepository.deleteByIdAndUserId(addressId, userId);
    }

    private String displayName(String fullName, String name) {
        return fullName == null || fullName.isBlank() ? name : fullName;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
