package com.velora.website.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.velora.website.Entity.ThuongHieu;
import com.velora.website.Repository.ThuongHieuRepository;

import lombok.RequiredArgsConstructor; // 1. IMPORT LOMBOK

@RestController
@RequestMapping("/api/thuong-hieu")
@CrossOrigin("*") // Mở CORS để Vue tải được dữ liệu
@RequiredArgsConstructor // 2. ANNOTATION TỰ ĐỘNG TIÊM DEPENDENCY
public class ThuongHieuController {
    
    // 3. ĐÃ XÓA @Autowired VÀ THÊM final
    private final ThuongHieuRepository thuongHieuRepository;

    @GetMapping
    public ResponseEntity<List<ThuongHieu>> getAllThuongHieu() {
        return ResponseEntity.ok(thuongHieuRepository.findAll());
    }
}