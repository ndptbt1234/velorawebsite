package com.velora.website.Controller;

import com.velora.website.Entity.SanPham;
import com.velora.website.Repository.SanPhamRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/san-pham")
@CrossOrigin("*") // Mở cửa cho Vue ở cổng khác gọi vào
@RequiredArgsConstructor // GẮN LOMBOK VÀO ĐỂ TỰ ĐỘNG TIÊM DEPENDENCY
public class SanPhamController {
    
    // THÊM CHỮ final ĐỂ LOMBOK TỰ ĐỘNG INJECT
    private final SanPhamRepository sanPhamRepository;

    // Lấy tất cả sản phẩm
    @GetMapping
    public List<SanPham> getAll() {
        return sanPhamRepository.findAll();
    }

    // Lấy sản phẩm theo ID (dùng cho trang chi tiết sau này)
    @GetMapping("/{id}")
    public SanPham getById(@PathVariable Integer id) {
        return sanPhamRepository.findById(id).orElse(null);
    }

  @PutMapping("/{id}/cap-nhat-kho")
    public ResponseEntity<?> capNhatKhoMoiNhat(
            @PathVariable Integer id, 
            @RequestBody Map<String, Object> request) { // Đổi từ Integer sang Object để tránh bị Spring Boot chặn
        
        try {
            // Lấy dữ liệu gửi lên và kiểm tra rỗng
            Object slObj = request.get("soLuongMoi");
            if (slObj == null || slObj.toString().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Số lượng không được để trống!");
            }

            // Ép kiểu an toàn sang số nguyên
            Integer soLuongMoi = Integer.parseInt(slObj.toString());
            
            if (soLuongMoi < 0) {
                return ResponseEntity.badRequest().body("Số lượng không được nhỏ hơn 0!");
            }

            // Tìm sản phẩm trong DB
            SanPham sanPham = sanPhamRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

            // Lưu đè số lượng mới
            sanPham.setSoLuongTonKho(soLuongMoi);
            
            // Tự động điều chỉnh trạng thái
            if (soLuongMoi > 0) {
                sanPham.setTrangThai("CON_HANG");
            } else {
                sanPham.setTrangThai("HET_HANG");
            }

            // Lưu vào CSDL
            sanPhamRepository.save(sanPham);
            
            return ResponseEntity.ok(sanPham);
            
        } catch (NumberFormatException e) {
            // Nếu người dùng gửi lên chữ cái thay vì số
            return ResponseEntity.badRequest().body("Vui lòng nhập định dạng số hợp lệ!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi Server: " + e.getMessage());
        }
    }
}