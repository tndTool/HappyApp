const express = require("express");
const nodemailer = require("nodemailer");
const User = require("../models/user.js");
const Sensor = require("../models/sensor.js");

const router = express.Router();

function generateOTP() {
  return Math.floor(1000 + Math.random() * 9000).toString();
}

// Setup nodemailer:
const transporter = nodemailer.createTransport({
  service: "gmail",
  host: ["smtp.gmail.com", "smtp.mailinator.com", "smtp.yopmail.com"],
  port: 587,
  secure: false,
  auth: {
    user: process.env.USER_NODEMAILER,
    pass: process.env.PASS_NODEMAILER,
  },
});

// Register:
router.post("/api/register", async (req, res) => {
  try {
    const { email } = req.body;

    const existingUser = await User.findOne({ email });

    if (existingUser && existingUser.isVerified) {
      return res
        .status(401)
        .json({ success: false, error: "Email already exists." });
    }

    const { name, password } = req.body;

    if (name.length > 30) {
      return res.status(400).json({
        success: false,
        error: "Name should be less than 30 characters.",
      });
    }

    if (password.length < 8 || password.length > 16) {
      return res.status(400).json({
        success: false,
        error: "Password should be between 8 and 16 characters long.",
      });
    }

    if (existingUser && !existingUser.isVerified) {
      existingUser.name = name;
      existingUser.password = password;
      existingUser.otp = generateOTP();
      await existingUser.save();

      const mailOptions = {
        from: process.env.EMAIL_FROM,
        to: email,
        subject: "Account Verification",
        html: `
          <h1>Welcome to Happy App!</h1>
          <p>Thank you for registering an account with us.</p>
          <p>Please use the verification code below to complete your account setup:</p>
          <h2 style="color: #ff0000;">OTP: ${existingUser.otp}</h2>
          <p>If you did not sign up for an account, please ignore this email.</p>
          <p>Best regards,</p>
          <p>The Happy App Team</p>
        `,
      };

      await transporter.sendMail(mailOptions);

      return res.status(200).json({
        success: true,
        message:
          "Verification code sent. Please check your email for the verify code.",
      });
    }

    const otp = generateOTP();

    const user = new User({
      name,
      email,
      password,
      otp,
    });

    await user.save();

    const mailOptions = {
      from: process.env.EMAIL_FROM,
      to: email,
      subject: "Account Verification",
      html: `
      <h1>Welcome to Happy App!</h1>
      <p>Thank you for registering an account with us.</p>
      <p>Please use the verification code below to complete your account setup:</p>
      <h2 style="color: #ff0000;">OTP: ${otp}</h2>
      <p>If you did not sign up for an account, please ignore this email.</p>
      <p>Best regards,</p>
      <p>The Happy App Team</p>
      `,
    };

    await transporter.sendMail(mailOptions);

    res.status(200).json({
      success: true,
      message:
        "User registered successfully. Please check your email for verification.",
    });
  } catch (error) {
    res.status(500).json({ success: false, error: "Internal server error." });
  }
});

router.post("/api/verify", async (req, res) => {
  try {
    const { email, otp } = req.body;

    const user = await User.findOne({ email, otp });

    if (!user) {
      return res.status(404).json({ success: false, error: "Invalid OTP." });
    }

    user.isVerified = true;

    await user.save();

    res
      .status(200)
      .json({ success: true, message: "Email verified successfully." });
  } catch (error) {
    res.status(500).json({ success: false, error: "Internal server error." });
  }
});

router.post("/api/resendOtp", async (req, res) => {
  try {
    const { email } = req.body;

    let user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({ success: false, error: "User not found." });
    }

    const otp = generateOTP();

    user.otp = otp;
    await user.save();

    const mailOptions = {
      to: email,
      from: {
        name: "Happy App",
        email: process.env.EMAIL_FROM,
      },
      subject: "Account Verification",
      html: `
      <h1>Welcome to Happy App!</h1>
      <p>Thank you for registering an account with us.</p>
      <p>Please use the verification code below to complete your account setup:</p>
      <h2 style="color: #ff0000;">OTP: ${otp}</h2>
      <p>If you did not sign up for an account, please ignore this email.</p>
      <p>Best regards,</p>
      <p>The Happy App Team</p>
      `,
    };

    await transporter.sendMail(mailOptions);

    res.status(200).json({
      success: true,
      message: "OTP sent successfully. Please check your email.",
    });
  } catch (error) {
    res.status(500).json({ success: false, error: "Internal server error." });
  }
});

// Login:
router.post("/api/login", async (req, res) => {
  try {
    const { email, password } = req.body;

    const user = await User.findOne({ email });

    if (!user) {
      return res
        .status(404)
        .json({ success: false, error: "Invalid password or username." });
    }

    if (!user.isVerified) {
      return res
        .status(401)
        .json({ success: false, error: "Invalid password or username." });
    }

    if (user.password !== password) {
      return res
        .status(401)
        .json({ success: false, error: "Invalid password or username." });
    }

    res.status(200).json({ success: true, message: "Login successful." });
  } catch (error) {
    res.status(500).json({ success: false, error: "Internal server error." });
  }
});

// Forgot password:
router.post("/api/forgotpassword/sendotp", async (req, res) => {
  try {
    const { email } = req.body;

    let user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({ success: false, error: "User not found." });
    }

    const otp = generateOTP();

    user.otp = otp;
    await user.save();

    const mailOptions = {
      to: email,
      from: {
        name: "Happy App",
        email: process.env.EMAIL_FROM,
      },
      subject: "Password Reset OTP",
      html: `
        <h1>Happy App Password Reset</h1>
        <p>You have requested to reset your password.</p>
        <p>Please use the One-Time Password (OTP) below to reset your password:</p>
        <h2 style="color: #ff0000;">OTP: ${otp}</h2>
        <p>If you did not request a password reset, please ignore this email.</p>
        <p>Best regards,</p>
        <p>The Happy App Team</p>
      `,
    };

    await transporter.sendMail(mailOptions);

    res.status(200).json({
      success: true,
      message: "OTP sent successfully. Please check your email.",
    });
  } catch (error) {
    res.status(500).json({ success: false, error: "Internal server error." });
  }
});

router.post("/api/forgotpassword/verifyotp", async (req, res) => {
  try {
    const { email, otp } = req.body;

    const user = await User.findOne({ email, otp });

    if (!user) {
      return res.status(404).json({ success: false, error: "Invalid OTP." });
    }

    res
      .status(200)
      .json({ success: true, message: "OTP verified successfully." });
  } catch (error) {
    res.status(500).json({ success: false, error: "Internal server error." });
  }
});

router.post("/api/forgotpassword/resetpassword", async (req, res) => {
  try {
    const { email, password } = req.body;

    const user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({ success: false, error: "Invalid email." });
    }

    if (password.length < 8 || password.length > 16) {
      return res.status(400).json({
        success: false,
        error: "Password should be between 8 and 16 characters long.",
      });
    }

    user.password = password;
    await user.save();

    res
      .status(200)
      .json({ success: true, message: "Password reset successfully." });
  } catch (error) {
    res.status(500).json({ success: false, error: "Internal server error." });
  }
});

// Get player info:
router.get("/api/user/:email", async (req, res) => {
  try {
    const email = req.params.email;

    const user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({ error: "User not found" });
    }

    const userInfo = {
      name: user.name,
      email: user.email,
      joinDate: user.createdAt,
    };

    res.status(200).json(userInfo);
  } catch (error) {
    res.status(500).json({ success: false, error: "Internal server error." });
  }
});

module.exports = router;

// Change name:
router.put("/api/user/changename", async (req, res) => {
  try {
    const { email, name } = req.body;

    const user = await User.findOne({ email });

    if (name.length > 30) {
      return res.status(400).json({
        success: false,
        error: "Name should be less than 30 characters.",
      });
    }

    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }

    user.name = name;
    await user.save();

    res.status(200).json({ message: "Name changed successfully", user });
  } catch (err) {
    res.status(500).json({ success: false, error: "Internal server error." });
  }
});

// Change password:
router.post("/api/user/changepassword", async (req, res) => {
  try {
    const { email, password } = req.body;

    const user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({ success: false, error: "Invalid email." });
    }

    if (password.length < 8 || password.length > 16) {
      return res.status(400).json({
        success: false,
        error: "Password should be between 8 and 16 characters long.",
      });
    }

    user.password = password;
    await user.save();

    res
      .status(200)
      .json({ success: true, message: "Password change successfully." });
  } catch (error) {
    res.status(500).json({ success: false, error: "Internal server error." });
  }
});

// Save sensor data:
router.post("/api/sensor/data", async (req, res) => {
  try {
    const { email, values } = req.body;

    const user = await User.findOne({ email });

    if (!user) {
      return res.status(400).json({ message: "User not found" });
    }

    const sensorData = values.map(({ sensor, value }) => ({
      sensor,
      value,
    }));

    const newData = new Sensor({
      email,
      values: sensorData,
    });

    await newData.save();

    return res.status(201).json({ message: "Data saved successfully" });
  } catch (error) {
    console.error(error);
    return res.status(500).json({ message: "Internal server error" });
  }
});
