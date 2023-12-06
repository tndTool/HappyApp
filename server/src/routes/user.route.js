import express from "express";
import User from "../models/User.js";

const router = express.Router();

// Get player info:
router.get("/:email", async (req, res) => {
  try {
    const email = req.params.email;

    const user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({ error: "User not found!" });
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

// Change name:
router.put("/changename", async (req, res) => {
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
      return res
        .status(404)
        .json({ success: false, message: "User not found!" });
    }

    user.name = name;
    await user.save();

    res.status(200).json({ message: "Name changed successfully", user });
  } catch (err) {
    res.status(500).json({ success: false, error: "Internal server error." });
  }
});

// Change password:
router.post("/changepassword", async (req, res) => {
  try {
    const { email, password } = req.body;

    const user = await User.findOne({ email });

    if (!user) {
      return res.status(404).json({ success: false, error: "User not found!" });
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
      .json({ success: true, message: "Password change successfully." });
  } catch (error) {
    res.status(500).json({ success: false, error: "Internal server error." });
  }
});

export default router;
