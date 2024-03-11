INSERT INTO SYS_ACCOUNT(USERNAME, PASSWORD, FULLNAME, SEX, ROLE, PHONE_NUMBER, STATUS) VALUES('admin', '$2a$12$UGPx1eE9SzfvCDniYtwoZuQRzVdjHKkjbZcDKXO4.1Z/uGpOsFFVu', 'Quản trị hệ thống', 1, 'ADMIN', '0706820684', 1);

INSERT INTO SYS_CONFIG(CODE, NAME, VALUE, SORT) VALUES('EMAIL_HOST', 'Email host', 'smtp', 0);
INSERT INTO SYS_CONFIG(CODE, NAME, VALUE, SORT) VALUES('EMAIL_PORT', 'Email port', '587', 0);
INSERT INTO SYS_CONFIG(CODE, NAME, VALUE, SORT) VALUES('EMAIL_USERNAME', 'Email username', null, 0);
INSERT INTO SYS_CONFIG(CODE, NAME, VALUE, SORT) VALUES('EMAIL_PASSWORD', 'Email password', null, 0);
INSERT INTO SYS_CONFIG(CODE, NAME, VALUE, SORT) VALUES('TIMEOUT', 'Thời gian timeout', null, 0);
INSERT INTO SYS_CONFIG(CODE, NAME, VALUE, SORT) VALUES('PATH_UPLOAD', 'Thư mục lưu file upload', null, 0);
INSERT INTO SYS_CONFIG(CODE, NAME, VALUE, SORT) VALUES('SEND_MAIL_REPORT_DAILY', 'Gửi mail báo cáo hoạt động kinh doanh hàng ngày', null, 0);
INSERT INTO SYS_CONFIG(CODE, NAME, VALUE, SORT) VALUES('MAX_SIZE_UPLOAD', 'Dung lượng file tối đa cho phép upload', null, 0);
INSERT INTO SYS_CONFIG(CODE, NAME, VALUE, SORT) VALUES('EXTENSION_ALLOWED_UPLOAD', 'Định dạng file được phép upload', null, 0);

INSERT INTO SYS_LANGUAGES(CODE, KEY, VALUE) VALUES('en', 'pro.product.name', 'Product name');
INSERT INTO SYS_LANGUAGES(CODE, KEY, VALUE) VALUES('en', 'pro.product.list', 'List of products');
INSERT INTO SYS_LANGUAGES(CODE, KEY, VALUE) VALUES('en', 'pro.order.list', 'List of orders');
INSERT INTO SYS_LANGUAGES(CODE, KEY, VALUE) VALUES('en', 'pro.order.code', 'Code');
INSERT INTO SYS_LANGUAGES(CODE, KEY, VALUE) VALUES('vi', 'pro.product.name', 'Tên sản phẩm');
INSERT INTO SYS_LANGUAGES(CODE, KEY, VALUE) VALUES('vi', 'pro.product.list', 'Danh sách sản phẩm');
INSERT INTO SYS_LANGUAGES(CODE, KEY, VALUE) VALUES('vi', 'pro.order.list', 'Danh sách đơn hàng');
INSERT INTO SYS_LANGUAGES(CODE, KEY, VALUE) VALUES('vi', 'pro.order.code', 'Mã sản phẩm');