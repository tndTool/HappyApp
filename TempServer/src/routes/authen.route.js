import express from "express";
import User from "../models/User.js";
import transporter from "../configs/nodemailer.config.js";

const router = express.Router();

function generateOTP() {
  const otp = Math.floor(1000 + Math.random() * 9000).toString();
  const createdAt = Date.now();
  return { otp, createdAt };
}

// Register:
router.post("/register", async (req, res) => {
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

    if (password.length < 8) {
      return res.status(400).json({
        success: false,
        error: "Password should be more than 8 characters.",
      });
    }

    if (existingUser && !existingUser.isVerified) {
      existingUser.name = name;
      existingUser.password = password;
      existingUser.otp = generateOTP().otp;
      existingUser.otpCreatedAt = Date.now();
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
          "Verification code sent. Please check your email for the verification code.",
      });
    }

    const { otp, createdAt } = generateOTP();

    const user = new User({
      name,
      email,
      password,
      otp,
      otpCreatedAt: createdAt,
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

router.post("/verify", async (req, res) => {
  try {
    const { email, otp } = req.body;

    const user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({ success: false, error: "User not found." });
    }

    if (user.isVerified) {
      return res
        .status(400)
        .json({ success: false, error: "User already verified." });
    }

    const now = Date.now();
    const otpCreatedAt = user.otpCreatedAt;

    if (now - otpCreatedAt > 120000) {
      return res
        .status(400)
        .json({ success: false, error: "OTP has expired." });
    }

    if (user.otp !== otp) {
      return res.status(400).json({ success: false, error: "Invalid OTP." });
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

router.post("/resendOtp", async (req, res) => {
  try {
    const { email } = req.body;

    let user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({ success: false, error: "User not found." });
    }

    const now = Date.now();
    const otpCreatedAt = user.otpCreatedAt;

    if (now - otpCreatedAt > 120000) {
      const otp = generateOTP();
      user.otp = otp.otp;
      user.otpCreatedAt = otp.createdAt;
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
          <h2 style="color: #ff0000;">OTP: ${otp.otp}</h2>
          <p>If you did not sign up for an account, please ignore this email.</p>
          <p>Best regards,</p>
          <p>The Happy App Team</p>
        `,
      };

      await transporter.sendMail(mailOptions);

      return res.status(200).json({
        success: true,
        message: "OTP sent successfully. Please check your email.",
      });
    }

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
        <h2 style="color: #ff0000;">OTP: ${user.otp}</h2>
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
router.post("/login", async (req, res) => {
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
router.post("/forgotpassword/sendotp", async (req, res) => {
  try {
    const { email } = req.body;

    let user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({ success: false, error: "User not found." });
    }

    const { otp, createdAt } = generateOTP();

    user.otp = otp;
    user.otpCreatedAt = createdAt;
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

router.post("/forgotpassword/verifyotp", async (req, res) => {
  try {
    const { email, otp } = req.body;

    const user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({ success: false, error: "User not found." });
    }

    const now = Date.now();
    const otpCreatedAt = user.otpCreatedAt;

    if (now - otpCreatedAt > 120000) {
      return res
        .status(400)
        .json({ success: false, error: "OTP has expired." });
    }

    if (user.otp !== otp) {
      return res.status(400).json({ success: false, error: "Invalid OTP." });
    }

    res
      .status(200)
      .json({ success: true, message: "Email verified successfully." });
  } catch (error) {
    res.status(500).json({ success: false, error: "Internal server error." });
  }
});

router.post("/forgotpassword/resetpassword", async (req, res) => {
  try {
    const { email, password } = req.body;

    const user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({ success: false, error: "Invalid email." });
    }

    if (password.length < 8) {
      return res.status(400).json({
        success: false,
        error: "Password should be more than 8 characters.",
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

export default router;
