<template>
    <div class="admin-wrapper">
        <nav class="sidebar">
            <h2 class="brand">VELORA ADMIN</h2>
            <ul class="menu">
                <li v-for="item in menuItems" :key="item.name">
                    <router-link :to="item.link" active-class="active">
                        <i :class="item.icon"></i> {{ item.name }}
                    </router-link>
                </li>
            </ul>
            <div class="sidebar-bottom">
                <router-link to="/" class="exit"><i class="fa-solid fa-house"></i> Return</router-link>
                <button class="logout" @click="handleLogout"><i class="fa-solid fa-right-from-bracket"></i>
                    Logout</button>
            </div>
        </nav>

        <main class="content">
            <header class="header">
                <div class="header-left">
                    <h1>Quản Lý <span class="gold">Kho Hàng</span></h1>
                    <p>Cập nhật số lượng tồn kho trực tiếp và nhanh chóng.</p>
                </div>
            </header>

            <section class="table-container">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th style="width: 80px;">ID</th>
                            <th style="width: 90px;">Hình Ảnh</th>
                            <th>Tên Sản Phẩm</th>
                            <th style="width: 140px;">Trạng Thái</th>
                            <th style="width: 180px; text-align: center;">Tồn Kho Hiện Tại</th>
                            <th style="width: 110px;">Hành Động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="product in products" :key="product.maSanPham">
                            <td>#{{ product.maSanPham }}</td>
                            
                            <td>
                                <div class="img-wrapper">
                                    <img :src="getImageUrl(product.anhDaiDien)" :alt="product.tenSanPham" />
                                </div>
                            </td>
                            
                            <td class="product-name">{{ product.tenSanPham }}</td>
                            
                            <td>
                                <span class="status-badge" :class="product.trangThai === 'CON_HANG' ? 'in-stock' : 'out-stock'">
                                    {{ product.trangThai === 'CON_HANG' ? 'Còn Hàng' : 'Hết Hàng' }}
                                </span>
                            </td>

                            <td style="text-align: center;">
                                <input 
                                    type="number" 
                                    v-model="product.formSoLuongMoi" 
                                    class="form-input stock-input" 
                                    min="0" 
                                />
                            </td>

                            <td>
                                <button class="btn-confirm" @click="submitCapNhatKho(product)">
                                    <i class="fa-solid fa-floppy-disk"></i> Lưu
                                </button>
                            </td>
                        </tr>

                        <tr v-if="products.length === 0">
                            <td colspan="6" class="empty-state">Đang tải dữ liệu hoặc kho hàng trống...</td>
                        </tr>
                    </tbody>
                </table>
            </section>
        </main>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const menuItems = [
  { name: 'Dashboard', link: '/admin/dashboard', icon: 'fa-solid fa-gauge' },
  { name: 'Products', link: '/admin/products', icon: 'fa-solid fa-box-open' },
  { name: 'Users', link: '/admin/users', icon: 'fa-solid fa-users' },
  { name: 'Orders', link: '/admin/orders', icon: 'fa-solid fa-file-invoice' },
  { name: 'Inventory', link: '/admin/inventory', icon: 'fa-solid fa-boxes-stacked' },
];
const products = ref([]);

const getImageUrl = (img) => {
    if (!img) return '/img/default-watch.png';
    return img.startsWith('http') ? img : `/img/${img}`;
};

const handleLogout = () => {
    localStorage.removeItem('user');
    window.location.href = '/';
};

// Hàm tải dữ liệu và gán số lượng cũ vào formSoLuongMoi
const loadProducts = async () => {
    try {
        const res = await fetch('http://localhost:8080/api/san-pham');
        if (res.ok) {
            const data = await res.json();
            products.value = data.map(p => ({
                ...p,
                // Lấy tồn kho thực tế làm số mặc định trong ô input
                formSoLuongMoi: p.soLuongTonKho 
            }));
        }
    } catch (error) {
        console.error('Lỗi kết nối Backend:', error);
    }
};

// Hàm gửi dữ liệu cập nhật xuống Backend
const submitCapNhatKho = async (product) => {
    // Ép kiểu chắc chắn thành số để không bị lỗi 400 Bad Request ở Spring Boot
    const parsedSoLuong = parseInt(product.formSoLuongMoi, 10);

    if (isNaN(parsedSoLuong) || parsedSoLuong < 0) {
        alert("Vui lòng nhập một số lượng hợp lệ (không âm)!");
        return;
    }

    try {
        const res = await fetch(`http://localhost:8080/api/san-pham/${product.maSanPham}/cap-nhat-kho`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                soLuongMoi: parsedSoLuong
            })
        });

        if (res.ok) {
            alert(`Đã cập nhật tồn kho thành ${parsedSoLuong} thành công!`);
            loadProducts(); // Load lại để đồng bộ trạng thái Còn/Hết hàng
        } else {
            const errorText = await res.text();
            alert("Lỗi: " + errorText);
        }
    } catch (error) {
        console.error('Lỗi khi cập nhật kho:', error);
        alert("Lỗi kết nối máy chủ.");
    }
};

onMounted(() => {
    loadProducts();
});
</script>

<style scoped>
.admin-wrapper {
    display: flex;
    min-height: 100vh;
    background: #f4f1ea;
    font-family: sans-serif;
}

.sidebar {
    width: 260px;
    background: #3e332e;
    color: #fff;
    padding: 40px 20px;
    display: flex;
    flex-direction: column;
    flex-shrink: 0;
}

.brand {
    font-size: 18px;
    color: #d1aa68;
    margin-bottom: 50px;
    text-align: center;
    letter-spacing: 2px;
}

.menu li {
    margin-bottom: 20px;
    list-style: none;
}

.menu a {
    color: #ccc;
    text-decoration: none !important;
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px;
    border-radius: 6px;
    transition: 0.3s;
}

.menu a:hover,
.menu a.active {
    color: #d1aa68;
    background-color: rgba(209, 170, 104, 0.1);
}

.sidebar-bottom {
    margin-top: auto;
    border-top: 1px solid #5a4b44;
    padding-top: 20px;
}

.exit,
.logout {
    color: #aaa;
    background: none;
    border: none;
    cursor: pointer;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 15px;
    text-decoration: none !important;
}

.content {
    flex: 1;
    padding: 60px;
    min-width: 0;
}

.gold {
    color: #d1aa68;
}

.header {
    margin-bottom: 40px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header h1 {
    color: #3e332e;
    font-size: 32px;
    margin-bottom: 5px;
}

.header p {
    color: #888;
    font-size: 14px;
}

.table-container {
    background: #ffffff;
    border: 1px solid #e0dcd5;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.02);
}

.admin-table {
    width: 100%;
    border-collapse: collapse;
    text-align: left;
}

.admin-table th {
    background-color: #fcfbf9;
    color: #3e332e;
    padding: 18px 20px;
    font-size: 13px;
    text-transform: uppercase;
    border-bottom: 2px solid #e0dcd5;
    letter-spacing: 1px;
}

.admin-table td {
    padding: 15px 20px;
    border-bottom: 1px solid #f0efeb;
    vertical-align: middle;
    color: #555;
    font-size: 14px;
}

.admin-table tbody tr:hover {
    background-color: #fdfaf5;
}

.img-wrapper {
    width: 50px;
    height: 50px;
    background: #f4f1ea;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
}

.img-wrapper img {
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
}

.product-name {
    font-weight: bold;
    color: #3e332e;
    max-width: 250px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.status-badge {
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 11px;
    font-weight: bold;
    text-transform: uppercase;
    display: inline-block;
    text-align: center;
    width: 90px;
}

.in-stock {
    background-color: #e8f5e9;
    color: #2e7d32;
}

.out-stock {
    background-color: #ffebee;
    color: #c62828;
}

/* Tinh chỉnh ô input số lượng */
.form-input.stock-input {
    width: 100px;
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 16px;
    font-weight: bold;
    outline: none;
    text-align: center;
    color: #3e332e;
    background: #fff;
    transition: all 0.3s;
}

.form-input.stock-input:focus {
    border-color: #d1aa68;
    box-shadow: 0 0 5px rgba(209, 170, 104, 0.3);
}

.btn-confirm {
    background-color: #d1aa68;
    color: #111;
    border: none;
    padding: 8px 14px;
    font-weight: bold;
    border-radius: 4px;
    cursor: pointer;
    transition: 0.2s;
    display: flex;
    align-items: center;
    gap: 6px;
    width: 100%;
    justify-content: center;
}

.btn-confirm:hover {
    background-color: #b8955b;
    color: #fff;
}

.empty-state {
    text-align: center;
    padding: 40px !important;
    color: #888;
}
</style>