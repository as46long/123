#!/bin/bash
set -e

echo "========================================"
echo "乐语匣子音乐平台 - 启动诊断脚本"
echo "========================================"
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查命令是否存在
check_command() {
    if command -v $1 &> /dev/null; then
        echo -e "${GREEN}✓${NC} $1 已安装"
        return 0
    else
        echo -e "${RED}✗${NC} $1 未安装"
        return 1
    fi
}

# 检查端口是否被占用
check_port() {
    if lsof -i :$1 &> /dev/null || netstat -tlnp 2>/dev/null | grep -q ":$1"; then
        echo -e "${GREEN}✓${NC} 端口 $1 正在监听"
        return 0
    else
        echo -e "${YELLOW}!${NC} 端口 $1 未监听"
        return 1
    fi
}

echo "【1】检查环境依赖..."
echo ""
ENV_OK=true
check_command java || ENV_OK=false
check_command mvn || ENV_OK=false
check_command mysql || ENV_OK=false
check_command node || ENV_OK=false
check_command npm || ENV_OK=false
echo ""

if [ "$ENV_OK" = false ]; then
    echo -e "${RED}请先安装缺失的依赖${NC}"
    exit 1
fi

echo "【2】检查服务状态..."
echo ""
check_port 3306 || echo "   MySQL 服务可能未启动"
check_port 8080 || echo "   后端服务可能未启动"
check_port 3000 || echo "   前端服务可能未启动"
echo ""

echo "【3】检查数据库..."
echo ""
if mysql -u root -p123456 -e "USE leyu_music; SHOW TABLES;" 2>/dev/null; then
    echo -e "${GREEN}✓${NC} 数据库连接正常"
else
    echo -e "${RED}✗${NC} 数据库连接失败或未初始化"
    echo "   请执行: mysql -u root -p < /workspace/docs/database_init.sql"
fi
echo ""

echo "【4】检查管理员账户..."
echo ""
if mysql -u root -p123456 leyu_music -e "SELECT id, username, role, status FROM t_admin WHERE username='admin';" 2>/dev/null; then
    echo -e "${GREEN}✓${NC} 管理员账户存在"
else
    echo -e "${YELLOW}!${NC} 管理员账户可能不存在或数据库未初始化"
fi
echo ""

echo "========================================"
echo "诊断完成"
echo "========================================"
echo ""
echo "如需修复管理员密码，请执行:"
echo "  mysql -u root -p123456 leyu_music -e \"UPDATE t_admin SET password = '\$2a\$10\$UMJqo3WtbiBxgATqrn58Se5.tZdR3d6VV3MtcDgM7xssKPcdcXW8G' WHERE username = 'admin';\""
echo ""
echo "启动后端服务:"
echo "  cd /workspace/leyu-admin-backend && mvn spring-boot:run"
echo ""
echo "启动前端服务:"
echo "  cd /workspace/leyu-admin-web && npm install && npm run dev"
