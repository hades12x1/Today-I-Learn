```markdown
- MySQL tuning:
  + Mức độ Database: table, query, configure setting
  + Mức độ hardware: CPU, Io, memory
- Mức độ database:
    - Xem xét về các yếu tố: cấu trúc bảng, column type, column size, index, storage engnine
    - Cơ chế lock strategy
    - Config đã tận dụng tối đa được phần cứng hay chưa: buffer, pool, instant, version
- Mức độ hardware:
    - Disk seeks: loại phần cứng sử dụng
    - Disk reading and writing
    - Cpu cycles
    - Memory ram
    - Bandwidth netword instant.
- Kiến trúc MYSQL:
    - 3 Layer
        + Utility layer:  
            Connection pool -> Parser -> Check permission
        + SQL Layer:
            Query cache | Rewriter -> Optimizer -> Executor
        + Storage Engine layer:
            Inno DB | MyISAM -> Memory
- Tunning:
    - Sử dụng công cụ để đo lại sử thay đổi sau mỗi lần config
        - Link tool: [https://github.com/major/MySQLTuner-perl](https://github.com/major/MySQLTuner-perl)
        - Link config slow log: [https://www.prisma.io/dataguide/mysql/reading-and-querying-data/identifying-slow-queries](https://www.prisma.io/dataguide/mysql/reading-and-querying-data/identifying-slow-queries)
        - File config mysql ver 8.x: `/etc/mysql/mysql.conf.d/mysqld.cnf`
    - Test thay đổi từng chút một
    - Các khái niệm cơ bản:
        + Buffer pool: Vùng nhớ chính để InnoDB cache table và index, thông thường chiếm 80% bộ nhớ
            - Data trong pool được lưu trữ dưới dạng page(16kb)
            - Chia thành 2 loại: Dirty(Chưa flush đến disk) và Clear page
            - Triển khai dạng linked list, theo thuật toán LRU
        + Log buffer: là một vùng lưu trũ trong bộ nhớ lưu các thông tin thay đổi để ghi xuống file ổ cứng, default 16MB
    - EXPLAIN QUERY:
      + EXPLAIN tập trung vào thông tin về execution plan
      + Mysql 8.0.18 giới thiệu Explain analyze cung cấp thông tin chi tiết thời gian thực thi
    - Mô tả các type trong explain query:
      + id: 
          - SELECT identifier. Trong trường hợp bạn sử dụng câu truy vấn lồng nhau thì các câu SELECT sẽ được đánh thứ tự để phân biệt.
      + select_type: 
          - Cột này chỉ ra dòng này là một SELECT phức tạp hay đơn giản.
          - Nếu nhãn là SIMPLE có nghĩa đây là dạng truy vấn đơn giản, không sử dụng UNION hay SUBQUERIES
          - Nếu truy vấn có bất kỳ phần con phức tạp nào, phần ngoài cùng được gắn nhãn là PRIMARY và các phần khác có thể được gắn nhãn như sau
            - SUBQUERY: SELECT trong SUBQUERY đặt trong mệnh đề SELECT
            - DERIVED: DERIVED: SELECT trong SUBQUERY đặt trong mệnh đề FROM. MySQL gọi đây là "bảng dẫn xuất"
            - UNION: Các SELECT thứ hai hoặc tiếp theo trong một UNION. SELECT đầu tiên đã được gắn nhãn PRIMARY.
            - UNION RESULT: SELECT được sử dụng để truy xuất kết quả từ bảng tạm thời ẩn danh của UNION
      + table:
          - tên table, mà dòng output đang tham khảo tới
      + type:
          - const hoặc system: Bảng chỉ có duy nhất 1 row phù hợp với điều kiện tìm kiếm theo điều kiện id
          - eq_ref: tìm kiếm với index type là PRIMARY KEY, UNIQUE NOT NULL, với toán tử =
          - ref: index type không phải PRIMARY KEY hoặc UNIQUE, với toán tử =, <=>
          - range: quét phạm vi là quét chỉ mục giới hạn, trả về các hàng phù hợp với range
          - index: index scan. Ưu điểm chính của loại này là tránh được việc sorting. Nhược điểm là chi phí đọc toàn bộ bảng theo thứ tự chỉ mục. Điều này thường có nghĩa là truy cập các hàng theo thứ tự ngẫu nhiên rất tốn kém
          - ALL: table scan
      + possible_keys: liệt kê tất cả các indexes liên quan có thể có để tìm các dòng trong table. Các column này có hoặc không sử dụng trong thực tế.
      + key: là các cột indexes thực thế mà MySQL quyết định sử dụng. Cột này có thể chứa khoá không liệt kê trong possible_keys.
      + ref: hiển thị các cột hoặc các hằng số được so sánh với index trong cột key
      + rows: thể hiện số rows mà MySQL dự kiến sẽ duyệt qua để thực thi câu query. Con số này là estimate, không chính xác.
      + filtered: thể hiện tỷ lệ phần trăm dự kiến các hàng của table được filtered bởi điều kiện. Giá trị lớn nhất là 100: tức là không có quá trình lọc hàng nào xảy ra.
      + extra: 
          - using index 
          - using where
          - using temporary(Sử dụng abrng tạm trong khi sắp sếp kết quả truy vấn)
          - Range checked

 for each record (index map:N): N): mySQL phát hiện không có good index, nhưng nhận thấy rằng một số index có thể được sử dụng sau khi các giá trị cột từ các bảng trước đó được biết đến
  - Không phải gộp query lúc nào cũng tốt, có thể tiết kiệm được số lần đóng mở connection, tuy nhiên với những query phức tạp sẽ gây khó cho quá trình parse query và analytic dẫn tới hiệu quả không tốt.
  - Không lấy nhiều hơn dữ liệu có nhu cầu sử dụng: Chiếm băng thông, cache size
  - Rút gọn điều điện where
  - Hạn chế function trên column => khong thể sử dụng index
  - Phân biệt được count(*) và count column cụ thể
  - Sử dụng toán tử like: không nên sử dụng ký tự % hay * đặt ở phía trước giá trị tìm kiếm
      + MySQL lưu trữ nội bộ các varchar như cây chỉ mục với ký hiệu đầu tiên là gốc và phân nhánh cho mỗi chữ cái tiếp theo.
      + where LIKE "Hello%": MySQL sẽ tìm các ký tự theo cây chỉ mục nhanh chóng. Ngược lại, với LIKE "%Hello", sẽ dẫn đến việc scan toàn bộ table
  - Hạn chế toán tử so sánh 2 lần:
      + WHERE emp_no >= 1000; bản chất thực hiện 2 query so sánh => emp_no > 999
  - Sử dụng join hợp lý

  Nguồn: [https://tech.cybozu.vn/mot-so-tips-de-toi-uu-hoa-cac-cau-truy-van-mysql-efd66/](https://tech.cybozu.vn/mot-so-tips-de-toi-uu-hoa-cac-cau-truy-van-mysql-efd66/)
```  