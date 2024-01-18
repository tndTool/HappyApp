<h1>About</h1>

<h3 style="color:white">Topic: <span style="color:#ccc">TRACKING HUMAN BEHAVIOR BY SMARTPHONE</span></h3>
<h3 style="color:white">Performer: <span style="color:#ccc">Nguyen Duc Toan, Nguyen Duc Trong</span></h3>
<h3 style="color:white">Advisor: <span style="color:#ccc">Ph.D. Phan Thanh Trung </span></h3>

<h3 style="color:white">Requirement:</h3>
<ul>
  <li>Authentication: login, register, forget password.</li>
  <li>Proactive tracking behavior: take photos, record videos and fill in the behavior</li>
  <li>Automated behavior tracking: collect sensor data on the user's phone</li>
  <li>View submitted behavior history</li>
  <li>Profile management: change name, change password, privacy manage, notification manage</li>
</ul>
<h3 style="color:white">Date: <span style="color:#ccc">15/09/2023 - present</span></h3>
<h3 style="color:white">Status: <span style="color:#d4edbc">Updating</span></h3>

<br>
<h1>Graduation Thesis</h1>

<h2 style="color:white">Project</h2>
<p style="text-align: justify">We name this application: <b>Happy App</b>. This thesis investigates human behavior in the context of smartphone usage, acknowledging the potential for tracking and analyzing these behaviors as a valuable resource for research in social activities such as eating, drinking, health tracking, fitness, etc. Recognizing that the understanding of human behavior in this digital realm extends beyond academia, it holds tangible real-world implications. By leveraging the data obtained from smartphone interactions, this research aims to provide insights that can be harnessed to enhance public health, encourage sustainable practices, and stimulate community engagement. In summary, human behavior in smartphone usage represents a multifaceted subject with significant implications for research, offering a means to gain valuable insights into our actions and their broader impact on society.The central concept behind this thesis revolves around the development of a smartphone application designed to collect data on students' eating and drinking behaviors.</p>

<h2 style="color:white">Directory Structure</h2>
<p>+ Project folder structure: <b>By kind</b></p>
<p>+ The project is divided into 2 parts: <b>Client uses Android Studio Java, Server uses NodeJS</b></p>

<h2 style="color:white">Table of Contents</h2>
<ul>
  <li>Installation</li>
  <li>Usage</li>
  <li>Technologies Used</li>
  <li>Contributing</li>
  <li>License</li>
</ul>

<h2 style="color:white">Installation</h2>
<p style="color:white">To install and run the client and server, follow these steps:</p>
<ol>
  <li>Clone the repository:</li>
  <pre><code>https://github.com/tndTool/HappyApp.git</code></pre>

  <li>Server folder:</li>
  <pre><code>
Using Visual Studio Code
--------------------------
Open terminal (you can use keyboard shortcut: Ctrl + J)
--------------------------
Type on terminal:
cd ../server
npm install
--------------------------
Fill in the .env file information completely according to the path D:\App\HappyApp\server\.env
--------------------------
Type on terminal:
npm start
</code></pre>

  <li>Client folder:</li>
<pre><code>
Using Android Studio
-------------------------- 
Sync Project with Gradle Files (you can use keyboard shortcut: Ctrl + Shift + O)
--------------------------
Open the ApiHelper.java file according to the path D:\App\HappyApp\client\app\src\main\java\com\example\happyapp\api
Replace the SERVER_URL link in code (Example: 'http://192.168.1.20:5000/api/' with 192.168.1.20 is IPv4 Address)
--------------------------
Open the virtual machine and run the application
</code></pre>
</ol>

<h2 style="color:white">Usage</h2>
<p style="color:white">To use the application, follow these steps:</p>
<ol>
  <li>Register an account to use.</li>
  <li>Click the + button to collect behavior with the camera.</li>
  <li>After completing the collection of user behavior with the camera, a popup will appear to choose whether to continue collecting behavior by video recording or not.</li>
  <li>The home page will display the history.</li>
  <li>The profile page to manage individual users.</li>
</ol>

<h2 style="color:white">Technologies Used</h2>
<p style="color:white">The following technologies were used to build this project:</p>
<ul>
  <li>Android Studio (Java)</li>
  <li>NodeJS</li>
  <li>MongoDB</li>
  <li>Cloudinary</li>
  <li>Nodemailer</li>
</ul>

<h2 style="color:white">Contributing</h2>
<p>Contributions are welcome! If you'd like to contribute to this project, please fork the repository and create a pull request. For major changes, please open an issue first to discuss what you would like to change.</p>

<h2 style="color:white">License</h2>
<p>This project is licensed under the <b>MIT License.</b></p>
