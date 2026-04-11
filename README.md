第一份：README.md (项目说明书)
请在项目根目录 (Project-Java-demo) 下新建一个文件 README.md，将以下内容复制进去。

员工任务管理系统
一个基于 Spring Boot + Vue 3 的全栈任务管理平台，实现了任务全生命周期管理、用户数据隔离及数据可视化导出功能。

✨ 项目演示
前端地址: http://localhost:5173
后端地址: http://localhost:8080
🛠️ 技术栈
后端
语言: Java 17
框架: Spring Boot 3.x
ORM: MyBatis
数据库: MySQL 8.0
构建工具: Maven
前端
框架: Vue 3 (Composition API)
构建工具: Vite
UI 组件库: Element Plus
网络请求: Axios
路由: Vue Router
运维与环境
容器化: Docker (用于部署 MySQL)
版本控制: Git
🚀 核心功能
用户模块
用户登录认证
数据隔离: 不同用户登录后仅能看到自己创建的任务
任务管理
全生命周期管理: 新增、编辑、删除任务
状态流转: 待办 -> 进行中 -> 已暂停 -> 已完成
批量操作: 支持批量删除、批量完成
时间管理:
设定截止日期，逾期任务自动标红并显示“逾期”标签
任务完成时自动记录实际完成时间
搜索与筛选: 支持按标题、描述模糊搜索
数据导出: 一键导出任务列表为 CSV/Excel 文件
📦 安装与运行
1. 环境准备
JDK 17+
Node.js 18+
Docker Desktop (运行 MySQL)
2. 数据库配置
使用 Docker 启动 MySQL 并初始化数据库：

docker run -d -p 3307:3306 -e MYSQL_ROOT_PASSWORD=root -v D:\Docker\DockerData\mysql:/var/lib/mysql --name mysql-local mysql:8.0
执行 sql/init.sql (需自行导出) 初始化表结构。

3. 后端启动
bash

cd backend
.\mvnw.cmd spring-boot:run
4. 前端启动
bash

cd frontend
npm install
npm run dev
📂 项目结构
text

Project-Java-demo/
├── backend/                # 后端 Spring Boot 项目
│   ├── src/main/java/      
│   └── pom.xml             
├── frontend/               # 前端 Vue 3 项目
│   ├── src/                
│   └── package.json        
└── README.md               
📝 开发日志
实现基于 MyBatis 的动态 SQL 搜索
解决 Java 17 与 Spring Boot 3 的版本兼容问题
实现 Docker 数据卷挂载，确保持久化存储
text


---

### **第二份：.gitignore (防止上传垃圾文件)**

在项目根目录 (`Project-Java-demo`) 下新建一个文件 `.gitignore`。这非常重要，能防止 `node_modules` 和 `target` 等庞大的编译文件上传到 GitHub。

```gitignore
# =====================
# 后端 Java 忽略文件
# =====================
.idea/
*.iml
target/
*.class
*.log
*.jar
.mvn/wrapper/maven-wrapper.jar

# =====================
# 前端 Vue 忽略文件
# =====================
node_modules/
dist/
.DS_Store
*.local

# =====================
# 系统文件
# =====================
Thumbs.db