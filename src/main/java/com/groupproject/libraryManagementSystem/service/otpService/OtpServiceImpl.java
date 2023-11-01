package com.groupproject.libraryManagementSystem.service.otpService;

import com.groupproject.libraryManagementSystem.dto.userDTO.response.VerifyResponse;
import com.groupproject.libraryManagementSystem.model.userEntity.Otp;
import com.groupproject.libraryManagementSystem.repository.OtpRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
@AllArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService{

    @Autowired
    private OtpRepository otpRepository;

    @Override
    @Transactional
    public VerifyResponse otpIsValidated(String email, String otp) {
        VerifyResponse m = new VerifyResponse();
        Date requestDate = new Date();
        try {
            log.info("Inside OTP VALIDATION.... VALIDATING EMAIL");
            if (!otp.isEmpty() && !email.isEmpty()){
                Otp userOtp = otpRepository.findByVerificationMail(email);
                if (userOtp != null){
                    log.info("Inside OTP VALIDATION.... VALIDATING OTP");
                    if (Integer.parseInt(otp) == userOtp.getOtp()){
                        log.info("Inside OTP VALIDATION.... VALIDATING EXPIRY TIME");
                        if (requestDate.before(userOtp.getExpiryTime())){
                            m.setMessage("User Validated Successfully");
                            m.setStatus("00");
                            //log.info("Inside OTP VALIDATION.... Deleting OTP RECORD when successful");
                            //otpRepository.deleteByVerificationMail(otpRequest.getEmail());
                        }
                        else {
                            m.setMessage("OTP Time is expired");
                            m.setStatus("99");
                            //log.info("Inside OTP VALIDATION.... Deleting OTP RECORD when time expired");
                            //otpRepository.deleteByVerificationMail(otpRequest.getEmail());
                        }
                    }
                    else {
                        m.setMessage("Invalid OTP!");
                        m.setStatus("99");
                    }
                }
                else {
                    m.setMessage("Invalid Email Supplied!");
                    m.setStatus("99");
                }

            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
        return m;

    }

    @Override
    public Integer generateOtp(String email) {

        if (!email.toLowerCase().isEmpty()){

            Otp otpEntity = otpRepository.findByVerificationMail(email);

            if(otpEntity == null){
                otpEntity = new Otp();
            }

            //Integer generatedOtp = ThreadLocalRandom.current().nextInt(1000, 10000);
            Integer generatedOtp = generateRandomOTP();
            Date generatedDate = new Date();
            otpEntity.setOtp(generatedOtp);
            otpEntity.setGeneratedTime(generatedDate);
            //OTP to expire in 2min
            otpEntity.setExpiryTime(new Date(generatedDate.getTime() + 2 * 60 * 1000));
            otpEntity.setVerificationMail(email);
            otpRepository.save(otpEntity);
            return generatedOtp;
        }
        return null;

//        Date today = new Date();
//        Date dueDate =

    }

    @Override
    public Otp getOtpDetailsByEmail(String email) {
        return otpRepository.findByVerificationMail(email);
    }

    @Override
    public void deleteOtpDetailsByEmail(String email) {
        otpRepository.deleteByVerificationMail(email);
    }

    public int generateRandomOTP() {
        Random random = new Random();
        int min = 1000;
        int max = 9999;
        return random.nextInt(max - min + 1) + min;
    }
}
