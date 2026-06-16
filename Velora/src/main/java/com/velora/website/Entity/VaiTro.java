package com.velora.website.Entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "VaiTro")
public class VaiTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maVaiTro;

    private String tenVaiTro;

    @ManyToMany(mappedBy = "vaiTros")
    @JsonIgnore // Khóa đầu này lại không cho quét ngược để tránh sinh lỗi 500
    private List<NguoiDung> nguoiDungs;

    // --- Getter và Setter ---
    public Long getMaVaiTro() { return maVaiTro; }
    public void setMaVaiTro(Long maVaiTro) { this.maVaiTro = maVaiTro; }
    public String getTenVaiTro() { return tenVaiTro; }
    public void setTenVaiTro(String tenVaiTro) { this.tenVaiTro = tenVaiTro; }
    public List<NguoiDung> getNguoiDungs() { return nguoiDungs; }
    public void setNguoiDungs(List<NguoiDung> nguoiDungs) { this.nguoiDungs = nguoiDungs; }
}