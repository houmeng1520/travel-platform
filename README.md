旅游平台（Travel Platform）
一个基于 Spring Boot + Vue 3 + MySQL 的前后端分离旅游平台，提供景点、美食、路线、酒店的浏览、搜索、预订、支付、评价等功能，并配有完善的管理后台。

线上地址：https://houmeng1520.github.io/travel-platform/

后端 API：https://travel-platform-production-d74a.up.railway.app

📖 项目简介
本项目是一个面向游客的一站式旅游服务平台，用户可以浏览旅游景点、特色美食、精选路线和酒店，并支持在线预订、支付和评价。管理员可以通过后台管理系统对平台所有数据进行增删改查、订单管理和用户管理。

核心特性
🏔️ 景点模块：景点列表、详情、搜索、门票预订

🍜 美食模块：美食列表、详情、搜索、套餐预订

🗺️ 路线模块：路线列表、详情、行程安排、报名

🏨 酒店模块：酒店列表、详情、搜索、预订、支付

📋 订单系统：统一订单管理，支持支付、取消、使用、评价

💬 评价系统：对已完成订单进行评分和评论

👤 用户系统：注册、登录、JWT 认证、个人中心、头像上传

⚙️ 管理后台：数据概览、景点/美食/路线/酒店/订单/用户管理、轮播图管理

🖼️ 图片上传：支持景点、美食、酒店、路线、轮播图的图片上传

🛠️ 技术栈
前端
技术	    说明
Vue 3	渐进式 JavaScript 框架
Vite	下一代前端构建工具
Element Plus	基于 Vue 3 的 UI 组件库
Vue Router	官方路由管理器（Hash 模式）
Pinia	状态管理库
Axios	HTTP 请求库
后端
技术	       说明
Spring Boot 3.x	   Java 后端框架
MyBatis-Plus	MyBatis 增强工具
MySQL 8.0	关系型数据库
JWT	用户认证令牌
BCrypt	密码加密
Lombok	简化 Java 代码
部署
服务	平台
前端	GitHub Pages
后端	Railway
数据库	Railway MySQL

travel-platform/
├── travel-backend/                 # 后端项目（Spring Boot）
│   ├── src/main/java/com/travel/
│   │   ├── common/                 # 通用类（Result 等）
│   │   ├── config/                 # 配置类（WebConfig 等）
│   │   ├── controller/             # 控制器层
│   │   ├── dto/                    # 数据传输对象
│   │   ├── entity/                 # 实体类
│   │   ├── mapper/                 # 数据访问层
│   │   ├── service/                # 业务层
│   │   │   └── impl/               # 业务实现类
│   │   └── utils/                  # 工具类（JwtUtils 等）
│   ├── src/main/resources/
│   │   └── application.yml         # 配置文件
│   └── pom.xml
│
├── travel-frontend/                # 前端项目（Vue 3）
│   ├── src/
│   │   ├── api/                    # API 接口定义
│   │   ├── assets/                 # 静态资源
│   │   ├── components/             # 公共组件
│   │   ├── router/                 # 路由配置
│   │   ├── stores/                 # Pinia 状态管理
│   │   ├── utils/                  # 工具函数
│   │   ├── views/                  # 页面视图
│   │   └── App.vue
│   ├── .env.development            # 开发环境变量
│   ├── .env.production             # 生产环境变量
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
│
├── .github/workflows/              # GitHub Actions 工作流
│   └── deploy.yml                  # 前端自动部署
│
└── README.md


##说明 🗄️ 数据库初始化

本项目未提供 SQL 初始化脚本，您可以通过以下方式创建数据库：

1. 根据 `travel-backend/src/main/java/com/travel/entity/` 下的实体类，使用 MyBatis-Plus 的代码生成器自动生成建表语句。
2. 或者参考 `travel-backend/src/main/resources/application.yml` 中的数据库配置，手动创建数据库 `travel_db`。
3. 然后运行后端项目，MyBatis-Plus 不会自动建表，需要您自行建表。建议使用代码生成器或根据实体类编写 SQL。
***********如果您是初学者，需要项目练手，欢迎联系作者交流。

环境要求
JDK 17+

Node.js 16+

MySQL 8.0+

Maven 3.6+


 联系方式
作者：Houmeng1520

邮箱：houmeng1520@gmail.com

GitHub：https://github.com/houmeng1520

⭐ 如果这个项目对您有帮助，欢迎给个 Star！