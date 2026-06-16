package com.velora.website.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.velora.website.Entity.NguoiDung;
import com.velora.website.Repository.NguoiDungRepository;
import com.velora.website.Request.LoginRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AuthController {

    private final NguoiDungRepository nguoiDungRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("--- ĐANG DEBUG LOGIN ---");
        System.out.println("Email nhận được: " + loginRequest.getEmail());
        
        // 1. Tìm người dùng trong Database
        Optional<NguoiDung> userOpt = nguoiDungRepository.findByEmail(loginRequest.getEmail());

        if (!userOpt.isPresent()) {
            System.out.println("LỖI: Không tìm thấy email này trong DB!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai email hoặc mật khẩu!");
        }

        NguoiDung user = userOpt.get();
        System.out.println("Đã tìm thấy User: " + user.getHoTen());

        // 2. So sánh mật khẩu Bcrypt
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean isMatch = encoder.matches(loginRequest.getPassword(), user.getMatKhauMaHoa());
        
        System.out.println("Kết quả so sánh (matches): " + isMatch);

        // 3. Xử lý khi đăng nhập thành công
        if (isMatch) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("maNguoiDung", user.getMaNguoiDung());
            responseData.put("hoTen", user.getHoTen());
            responseData.put("email", user.getEmail());
            
            // 🛠️ ĐỘNG HÓA PHÂN QUYỀN TRỰC TIẾP TỪ DATABASE:
            // Lấy vai trò đầu tiên của user trong List<VaiTro> đổ ra, nếu rỗng thì mặc định là CUSTOMER
            String roleName = "ROLE_CUSTOMER";
            if (user.getVaiTros() != null && !user.getVaiTros().isEmpty()) {
                roleName = user.getVaiTros().get(0).getTenVaiTro();
            }
            responseData.put("vaiTro", roleName);

            System.out.println("=> DỮ LIỆU ĐÓNG GÓI GỬI VỀ VUE.JS: " + responseData);
            return ResponseEntity.ok(responseData); 
            
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai email hoặc mật khẩu!");
        }
    }
}