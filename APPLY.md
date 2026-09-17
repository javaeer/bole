# 应用说明 / How to apply

本目录包含为 `javaeer/bole` 准备的开源项目资料，已按仓库根目录的相对路径整理好。

## 方式一：直接复制到仓库（推荐）

```bash
# 在你的 bole 仓库根目录执行（请把 /path/to/bole-opensource 换成实际路径）
cp -r /path/to/bole-opensource/.  /path/to/bole/

cd /path/to/bole
git add -A
git commit -m "docs: 完善开源项目资料（README/许可证/贡献指南/CI/架构等）"
git push origin main
```

## 方式二：用 git 补丁（若你更喜欢 diff）

```bash
# 在原克隆仓库已提交好的情况下，导出补丁：
git -C /tmp/bole format-patch --binary -1 HEAD --stdout > bole-opensource.patch
# 在目标仓库应用：
git apply bole-opensource.patch
```

## 应用后建议

1. 在 GitHub 仓库 **Settings → General** 填写：
   - Description：`千里马常有，而伯乐不常有 · 开源个人职业档案与简历生成平台`
   - Website：（你的线上地址，可选）
   - Topics：`resume`, `vue`, `spring-boot`, `uniapp`, `mybatis-plus`, `minio`, `postgresql`
2. 在 **Settings → General → Features** 开启 Issues / Discussions / Wikis。
3. 在 **Security → Code security** 按需开启 Dependabot alerts。
4. 去 **Actions** 跑一次 CI（ci.yml），让 README 里的构建徽章亮起来。
5. 把 `docs/assets/preview-*.png` 替换成真实截图，让预览画廊更可信。
