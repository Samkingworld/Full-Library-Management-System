package com.groupproject.libraryManagementSystem.service.userService;

import com.groupproject.libraryManagementSystem.dto.CustomResponseDto;
import com.groupproject.libraryManagementSystem.dto.userDTO.request.*;
import com.groupproject.libraryManagementSystem.dto.userDTO.response.AuthenticationResponse;
import com.groupproject.libraryManagementSystem.dto.userDTO.response.VerifyResponse;
import com.groupproject.libraryManagementSystem.model.userEntity.Role;
import com.groupproject.libraryManagementSystem.model.userEntity.Status;
import com.groupproject.libraryManagementSystem.model.userEntity.User;
import com.groupproject.libraryManagementSystem.repository.RoleRepository;
import com.groupproject.libraryManagementSystem.repository.UserRepository;
import com.groupproject.libraryManagementSystem.service.authenticationService.TokenService;
import com.groupproject.libraryManagementSystem.service.otpService.OtpService;
import com.groupproject.libraryManagementSystem.util.EmailService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    OtpService otpService;
    @Autowired
    private EmailService sendmail;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @SneakyThrows
    @Override
    @Transactional(rollbackOn = Exception.class)
    public String registerUser(RegisterRequest registerRequest) {
        Date dob = new SimpleDateFormat("dd/MM/yyyy").parse(registerRequest.getDob());
        String message = "";

        Role userRole = roleRepository.findByAuthority("USER").get();
        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);

        User newUser = new User();

        if(registerRequest !=null){
            User findMatchingUser = userRepository.findByEmail(registerRequest.getEmail());
            if (findMatchingUser == null){
                newUser.setAuthorities(authorities);
                newUser.setStatus(Status.PENDING);
                newUser.setDob(dob);
                newUser.setEmail(registerRequest.getEmail());
                newUser.setAddress(registerRequest.getAddress());
                newUser.setFavoriteWord(registerRequest.getFavoriteWord());
                newUser.setFirstName(registerRequest.getFirstName());
                newUser.setLastName(registerRequest.getLastName());
                newUser.setPhoneNumber(registerRequest.getPhoneNumber());
//                newUser.setPassword(hashPassword(registerRequest.getPassword()));
                newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
                log.info("Saving new user with email "  + newUser.getEmail() + "  to database ");
                userRepository.save(newUser);
                log.info("User saved!");

                /** generating otp to be sent to participants**/
                Integer otp = otpService.generateOtp(newUser.getEmail());

                /** Sending email**/
                log.info("Composing email to send otp");
                String toEmail = newUser.getEmail();
                String subject = "ACCOUNT CREATION OTP ALERT";
                String body = "Hello " + newUser.getFirstName() + ", " + "\nThank you for registering with SamkingWorld Library. \nPlease enter the OTP below in the next link " +
                        "\nNote: Your OTP expires in 2min. \n \n OTP: \t\t" + otp + " \n \n If you didn't make this request, please ignore! \n Thank you." +
                        "\n Regards. \n\n";
                try {
                    log.info("sending otp to user with email: " + newUser.getEmail());

                    sendmail.sendMail(toEmail, subject, body);
                    message = "Check email for OTP to verify your account";
                }catch (Exception e){
                    log.error(e.getMessage());
                    deleteUserByEmail(registerRequest.getEmail());
                    otpService.deleteOtpDetailsByEmail(registerRequest.getEmail());
                    message = "Couldn't send otp please try registering again later";

                }
            }
            else {
                    message = "User with this email already exist.";
            }
        }
        return message;
    }

    @Override
    @CacheEvict(value = {"allUsers", "getUserByEmail", "getUserByUserId"}, allEntries = true)
    public VerifyResponse verifyUser(VerifyUserRequest verifyUserRequest) {
        VerifyResponse resMessage = new VerifyResponse();
        User user = userRepository.findByEmail(verifyUserRequest.getEmail());

        Role userRole = roleRepository.findByAuthority("USER").get();
        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);

        if (user != null){
            log.info("Validating OTP received from user with email " + verifyUserRequest.getEmail());
            resMessage = otpService.otpIsValidated(verifyUserRequest.getEmail(), verifyUserRequest.getOtp());
            log.info("OTP VALIDATION COMPLETED");
            if (resMessage.getStatus() == "00"){

                log.info("Generating USER_ID for " + verifyUserRequest.getEmail());
                Integer maxUserIdNumber = userRepository.findLastMembershipNo();
                maxUserIdNumber = (maxUserIdNumber == null) ? 10001 : maxUserIdNumber + 1;
                String customUserId = "SLB/LAG/" + maxUserIdNumber;
                user.setMembershipNo(customUserId);
                user.setAuthorities(authorities);
                user.setStatus(Status.VERIFIED);
                log.info("Saving User: " + user);
                userRepository.save(user);
                resMessage.setMessage(resMessage.getMessage() + "  Check your email for complete Details");

                /** Sending email**/
                log.info("Composing email to send USER DETAILS");
                String toEmail = verifyUserRequest.getEmail();
                String subject = "PROFILE CREATED SUCCESSFULLY!";
                String body = "Hello " + user.getFirstName() + ", " + "\nCongratulations on completing you profile registration with our Library. \nYour Profile has been successfully created " +
                        "\nNote: Your Details Below \n \n " +
                        "\n \tYour User Id is: \t\t " + user.getMembershipNo() +
                        "\n\n \tFull Name: \t\t " + user.getFirstName()+", " +user.getLastName() +
                        "\n \tAddress: \t\t " + user.getAddress() +
                        "\n\n \tPhone No: \t\t " + user.getPhoneNumber()  +
                        "\n\n \tEmail: \t\t " + user.getEmail()  +
                        "\n\n \tFavourite Word: \t\t" + user.getFavoriteWord() +
                        "\n\nCongratulation once again " +
                        "\n" +
                        "\n\nRegards\n" +
                        "\nOgoh Samuel Otor\n" +
                        "\nCEO";
                try {
                    log.info("sending WELCOME MESSAGE to user with email: " + user.getEmail());

                    sendmail.sendMail(toEmail, subject, body);
                }catch (Exception e){e.getMessage();}
            }

        }
        return resMessage;
    }

    @Override
    public AuthenticationResponse signIn(AuthenticationRequest authRequest){
        var user = userRepository.findByEmail(authRequest.getEmail());
        if (user == null){
            throw new IllegalArgumentException("Invalid email or password");
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            String token = tokenService.generateJwt(auth);
            return new AuthenticationResponse(token);

        } catch (Exception e){
            log.info(e.getMessage());
            System.out.println(e.getStackTrace());
            return new AuthenticationResponse("");
        }

    }

    @Override
    @Transactional()
    @CacheEvict(value = {"allUsers", "getUserByEmail", "getUserByMembershipNo"}, allEntries = true)
    public String deleteUserByEmail(String email) {
        User findUser = userRepository.findByEmail(email);
        if(findUser ==null || findUser.getEmail().isEmpty()){
            return "User does not exist";
        }
        userRepository.deleteByEmail(email);
        return "User Deleted Successfully.";
    }

    @Override
    @Transactional()
    @CacheEvict(value = {"allUsers", "getUserByEmail", "getUserByMembershipNo"})
    public String deleteByMembershipNo(String membershipNo) {
        User findUser = userRepository.findByMembershipNo(membershipNo);
        if(findUser ==null || findUser.getEmail().isEmpty()){
            return "User does not exist";
        }
        userRepository.deleteByMembershipNo(membershipNo);
        return "User Deleted Successfully.";

    }

    @Override
    public String resendOtp(String email) {
        String message = "";
        User findmatchingUser = userRepository.findByEmail(email);
        if (findmatchingUser == null) {
            message = "User does not exist";
        }
        else {
            if(findmatchingUser.getStatus() != Status.PENDING){
                message = "This Profile has already been created!.";
            }
            else {
                /** generating otp to be re-sent to participants**/
                Integer otp = otpService.generateOtp(email);

                /** Sending email**/
                log.info("Composing email to send otp");
                String toEmail = email;
                String subject = "ACCOUNT RE-VERIFICATION OTP ALERT";
                String body = "Hello " + findmatchingUser.getFirstName() + ", " + "\n\nPlease enter the OTP below in the next link to verify your account" +
                        "\nNote: Your OTP expires in 2min. \n \n OTP: \t\t" + otp + " \n \n If you didn't make this request, please ignore! \n Thank you." +
                        "\n Regards. \n\n";
                try {
                    log.info("re-sending otp to user with email: " + email);

                    sendmail.sendMail(toEmail, subject, body);
                    message = "Check email for OTP to verify your account";
                }catch (Exception e) {
                    log.error(e.getMessage());
                    deleteUserByEmail(email);
                    otpService.deleteOtpDetailsByEmail(email);
                    message = "Couldn't send otp please try registering again later";
                }
            }

        }
         return message;
    }

    @Override
    @Cacheable(value = "allUsers")
    public Page<User> getAllUsers(int page, int size, String sortField) {
        Sort sort = Sort.by(sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return userRepository.findAll(pageable);
    }


    @Override
    @Cacheable(value = "getUserByEmail", key = "#email")
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);

    }

    @Override
    @Cacheable(value = "getUserByMembershipNo", key = "#membershipNo")
    public User getUserByMembershipNo(String membershipNo) {
        return userRepository.findByMembershipNo(membershipNo);
    }

    @Override
    @CacheEvict(value = {"allUsers", "getUserByEmail", "getUserByMembershipNo"}, allEntries = true)
    public User updateUser(String email, UpdateUserRequest updateRequest) {
        User userToUpdate = userRepository.findByEmail(email);
        if(userToUpdate != null) {
            userToUpdate.setFirstName(updateRequest.getFirstName() != null && !updateRequest.getFirstName().isEmpty() ? updateRequest.getFirstName() : userToUpdate.getFirstName());
            userToUpdate.setLastName(updateRequest.getLastName() != null && !updateRequest.getLastName().isEmpty() ? updateRequest.getLastName() : userToUpdate.getLastName());
            userToUpdate.setPhoneNumber(updateRequest.getPhoneNumber() != null && !updateRequest.getPhoneNumber().isEmpty() ? updateRequest.getPhoneNumber() : userToUpdate.getPhoneNumber());
            userToUpdate.setAddress(updateRequest.getAddress() != null && !updateRequest.getAddress().isEmpty() ? updateRequest.getAddress() : userToUpdate.getAddress());
            userToUpdate.setFavoriteWord(updateRequest.getFavoriteWord() != null && !updateRequest.getFavoriteWord().isEmpty() ? updateRequest.getFavoriteWord() : userToUpdate.getFavoriteWord());
            return userRepository.save(userToUpdate);
        }
        else{
            return new User();
        }

    }

    @Override
    @CacheEvict(value = {"allUsers", "getUserByEmail", "getUserByMembershipNo"}, allEntries = true)
    public Map<String, Object> updateRole(String email, String role) {
        Map<String, Object> response = new HashMap<>();
        Role userRole = roleRepository.findByAuthority(role).get();
        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);
        User userToUpdate = userRepository.findByEmail(email);
        if (userToUpdate !=null){
            if(!role.isEmpty() && !role.isBlank()){
                role.trim().toUpperCase();
                if(role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("USER") || role.equalsIgnoreCase("LIBRARIAN")){
                    userToUpdate.setAuthorities(authorities);
                    response.put("updatedUserDetails", userRepository.save(userToUpdate));
                    response.put("message", "Role Updated Successfully!");

                }
                response.put("message", "Invalid Role Type!.. Role must be LIBRARIAN or ADMIN or MEMBER");

            }
        }
        else{
            response.remove("message");
            response.put("message", "User does not exist");
        }

        return response;
    }

    @Override
    public Object forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        CustomResponseDto responseDto = new CustomResponseDto();
        if (user == null){
            responseDto.setMessage("Invalid email");
            return responseDto;
        }

        if (user.getStatus() != Status.VERIFIED){
            responseDto.setMessage("Account Not Verified. Please verify you account first");
            return  responseDto;
        }

        /** generating otp to be sent to user for password recovery**/
        Integer otp = otpService.generateOtp(email);

        /** Sending email**/
        log.info("Composing email to send otp");
        String toEmail = email;
        String subject = "PASSWORD RECOVERY OTP ALERT";
        String body = "Hello " + user.getFirstName() + ", " + "\n\nPlease enter the OTP below in the next link to recover your password" +
                "\nNote: Your OTP expires in 2min. \n \n OTP: \t\t" + otp + " \n \n If you didn't make this request, please ignore! \n Thank you. \n" +
                "\n\n Regards. \n\n";
        try {
            log.info("sending otp to user with email: " + email);

            sendmail.sendMail(toEmail, subject, body);
            responseDto.setMessage("Check email for OTP to reset you password ");
            responseDto.setStatus("00");

        }catch (Exception e) {
            log.error(e.getMessage());
//            deleteUserByEmail(email);
 //           otpService.deleteOtpDetailsByEmail(email);
            responseDto.setMessage("Couldn't send otp please try again later");
        }

        return responseDto;
    }

    @Override
    public Object verifyPasswordInfo(ForgotPasswordRequest passwordRequest) {
        CustomResponseDto responseDto = new CustomResponseDto();
        User user = userRepository.findByEmail(passwordRequest.getEmail());
        if (user != null){
            log.info("Validating OTP received from user with email " + passwordRequest.getEmail());
            VerifyResponse otpResponse = otpService.otpIsValidated(passwordRequest.getEmail(), passwordRequest.getOtp());
            log.info("OTP VALIDATION COMPLETED");
            if (otpResponse.getStatus() == "00"){

                log.info("Resetting password for " + passwordRequest.getEmail());
                user.setPassword(hashPassword(passwordRequest.getNew_password()));
                log.info("Saving User");
                userRepository.save(user);

                /** Sending email**/
                log.info("Composing email to send USER DETAILS");
                String toEmail = passwordRequest.getEmail();
                String subject = "PASSWORD RESET COMPLETED";
                String body = "Hello " + user.getFirstName() + ", " + "\nPassword Successfully changed." +
                        "\n\nRegards\n" +
                        "Ogoh Samuel Otor\n" +
                        "CEO";
                try {
                    log.info("Alerting User for Password change successful to : " + user.getEmail());

                    sendmail.sendMail(toEmail, subject, body);
                    responseDto.setMessage("Password has been successfully reset");
                    responseDto.setStatus("00");
                    return responseDto;

                }catch (Exception e){
                    responseDto.setMessage(e.getMessage());
                    return responseDto;
                }
            }
            return otpResponse;
        }
        return responseDto;
    }


//    public List<FilterRequestResponse> filterByField(FilterRequestResponse filterRequest){
//        userRepository.findAll(Example.of(filterRequest));
//
//    }

    public String hashPassword(String password){
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return hashedPassword;
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword){
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

}


