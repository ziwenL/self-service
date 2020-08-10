<h3 align="center">基本介绍</h3>
本项目是基于 MVVM + Jetpack 设计模式和组件进行开发，主要通过无障碍服务来实现钉钉自动打卡功能，目前只适用于 MIUI 系统

##
### 功能介绍
<ul>
<li>
<p>
在用户授予相关权限并设置了考勤时间及锁屏密码后，该 APP 将会在指定时间自动唤醒屏幕并解锁，并启动钉钉进行打卡，然后息屏直到下一个打卡时间</p>
</li>
<li>
<p>
当今天所有打卡时间已过，自动停止前台服务，直到第二天凌晨将再次启动前台服务进行定时打卡
</p>
</li>
<li>
<p>
周末默认不启动定时打卡前台服务，此时如有需要可通自行启动服务，支持通过快捷方式启动
</p>
</li>
</ul>

### 具体实现
<ul>
<li>
<p>通过  <a href="https://developer.android.google.cn/reference/android/os/PowerManager?hl=en">PowerManager</a> 唤醒屏幕</p>
</li>
<li>
<p>使用 <a href="https://github.com/Tencent/MMKV">MMKV</a> 库存储手机解锁密码及考勤数据</p>
</li>
<li>
<p>通过 <a href="https://developer.android.google.cn/reference/android/accessibilityservice/AccessibilityService?hl=en">AccessibilityService</a> 模拟用户操作进行自动化解锁和打卡</p>
</li>
<li>
<p>通过 <a href="https://developer.android.google.cn/reference/android/app/admin/DevicePolicyManager?hl=en">DevicePolicyManager</a> 息屏</p>
</li>
<li>
<p>通过用户给予 <b>自启动权限 + 启用无障碍服务</b> 的方式进行保活</p>
</li>
</ul>

### 使用须知
<b>因为本项目初衷是开发来方便作者自身使用，所以适配性较差~<br>但是这些限定条件，均可在源码里根据原因自行调整到与你们实际情况相符后消除!!!</b>
<ul>
<li>
<p>目前该 APP 仅适配了 <b>MIUI 系统</b>，仅支持 <b>Android 7.0 或以上的 MIUI 系统</b><br>（因为解锁流程的自动化操作与系统 UI 界面有关）</p>
</li>
<li>
<p>目前支持的钉钉版本为 <b>5.1.16</b><br>（因为版本不同可能会因 UI 样式不同，导致模拟打下班卡时点击的按钮位置不正确）</p>
</li>
<li>
<p>MIUI 系统下需要授予 APP <b>后台弹出界面</b> 权限<br>
（MIUI 系统独有，需在应用详情页面手动授予）</p>
</li>
<li>
<p>实现保活效果，需要授予 APP <b>自启动</b> 权限</br>（需在应用详情页面手动授予）</p>
</li>
<li>
<p>默认手机有设置 <b>锁屏密码</b>，且是纯数字密码<br>（基于作者个人手机设置习惯而定制，影响解锁流程）</p>
</li>
<li>
<p>手机 <b>负一屏</b> 模式需设置为 <b>标准模式</b><br>（基于作者个人手机设置习惯而定制，影响解锁流程）</p>
</li>
<li>
<p>可能会因为手机设置了<b>防误触</b>，导致亮屏后无法解锁<br>（当手机在口袋里的时候）</p>
</li>
<li>
<p>钉钉内需将 <b>消息</b> 页面的 <b>智能工作助理</b> 置顶<br>（防止在消息页面无法找到该节点）</p>
</li>
<li>
<p>钉钉内需将 <b>上班卡</b> 设置成 <b>极速打卡</b> ， <b>下班卡</b> 设置成 <b>手动打卡</b><br>（极速打卡表示只需启动钉钉无需模拟用户操作，而手动打卡则需使用无障碍服务模拟用户操作）</p>
</li>
</ul>

### 使用步骤

