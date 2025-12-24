import sys
import os

# 添加当前目录到Python路径
sys.path.append(os.path.dirname(os.path.abspath(__file__)))


def test_environment():
    """测试环境配置"""
    from dotenv import load_dotenv
    load_dotenv()

    api_key = os.getenv("DEEPSEEK_API_KEY")
    print(f"✅ API密钥已加载: {'已设置' if api_key else '未设置'}")

    return bool(api_key)


def test_direct_api():
    """直接测试DeepSeek API"""
    import requests
    from dotenv import load_dotenv

    load_dotenv()

    api_key = os.getenv("DEEPSEEK_API_KEY")
    if not api_key:
        print("❌ 未找到API密钥，请在.env文件中设置DEEPSEEK_API_KEY")
        return False

    url = "https://api.deepseek.com/v1/chat/completions"
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json"
    }

    data = {
        "model": "deepseek-chat",
        "messages": [
            {"role": "user", "content": "你好！请简单回复'测试成功'"}
        ],
        "temperature": 0.7,
        "max_tokens": 50
    }

    try:
        print("🔄 正在测试API连接...")
        response = requests.post(url, json=data, headers=headers, timeout=10)

        if response.status_code == 200:
            result = response.json()
            content = result["choices"][0]["message"]["content"]
            print(f"✅ API测试成功！回复: {content}")
            return True
        else:
            print(f"❌ API请求失败: {response.status_code}")
            print(f"响应: {response.text}")
            return False

    except Exception as e:
        print(f"❌ API连接异常: {e}")
        return False


def test_companion():
    """测试智能学伴系统"""
    print("\n🔍 测试智能学伴系统...")

    try:
        # 创建学伴实例
        from main import LearningCompanionGraph

        companion = LearningCompanionGraph(
            session_id="test_session",
            clear_history=True
        )

        # 测试对话
        test_inputs = [
            "你好，我想学习Python编程",
            "什么是变量作用域？",
            "能给我一个循环的例子吗？"
            "我们能讨论一下数据结构的问题吗",
            "我们能讨论一下计算机组成原理的问题吗"
        ]

        for i, user_input in enumerate(test_inputs[:5]):  # 只测试前两个，避免消耗太多token
            print(f"\n💬 用户: {user_input}")
            response = companion.process_message(user_input)
            print(f"🤖 AI: {response[:100]}...")  # 只显示前100个字符

            # 检查响应
            if response and len(response) > 5:
                print(f"✅ 第{i + 1}轮对话测试成功")
            else:
                print(f"❌ 第{i + 1}轮对话测试失败")
                return False

        return True

    except ImportError as e:
        print(f"❌ 导入错误: {e}")
        print("请确保安装了所有依赖: pip install -r requirements.txt")
        return False
    except Exception as e:
        print(f"❌ 系统测试异常: {e}")
        import traceback
        traceback.print_exc()
        return False



if __name__ == "__main__":
    print("=" * 50)
    print("🤖 智能学伴系统测试开始")
    print("=" * 50)

    # 1. 测试环境
    env_ok = test_environment()

    # 2. 直接测试API（需要API密钥）
    if env_ok:
        api_ok = test_direct_api()
    else:
        api_ok = False
        print("⚠️  跳过API测试，因为没有API密钥")

    # 3. 测试学伴系统
    if api_ok:
        system_ok = test_companion()
    else:
        system_ok = False
        print("⚠️  跳过系统测试，因为API测试失败")

    print("\n" + "=" * 50)
    print("测试总结:")
    print(f"环境配置: {'✅ 通过' if env_ok else '❌ 失败'}")
    print(f"API连接: {'✅ 通过' if api_ok else '❌ 失败'}")
    print(f"系统功能: {'✅ 通过' if system_ok else '❌ 失败'}")
    print("=" * 50)