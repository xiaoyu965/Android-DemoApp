cmake_minimum_required(VERSION 3.4.1)

# 必须添加项目声明
project("ability")

# 设置源文件路径（使用相对路径）
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

# 添加库时使用正确的路径
add_library(
        native-lib
        SHARED
        jni_utils.cpp  # 确保文件名与实际一致
)

# 包含当前目录头文件
include_directories(${CMAKE_CURRENT_SOURCE_DIR})

target_link_libraries(
        native-lib
        android
        log
)