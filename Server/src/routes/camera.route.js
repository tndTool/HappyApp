import express from "express";
import BehaviorCamera from "../models/BehaviorCamera.js";
import { uploadImage } from "../configs/cloudinary.config.js";
import User from "../models/User.js";

const router = express.Router();

router.post("/behavior", uploadImage.single("image"), async (req, res) => {
  try {
    const { email, behavior, questions } = req.body;
    const image = req.file.path;

    const user = await User.findOne({ email });

    if (!user) {
      return res.status(400).json({ message: "User not found" });
    }

    if (!user.isVerified) {
      return res.status(400).json({ error: "User not found." });
    }

    const newBehaviorCamera = new BehaviorCamera({
      email,
      image,
      behavior,
      questions,
    });

    const savedBehaviorCamera = await newBehaviorCamera.save();

    res.status(201).json(savedBehaviorCamera);
  } catch (error) {
    console.error(error);
    res
      .status(500)
      .json({ error: "An error occurred while creating the behavior object" });
  }
});

router.get("/behavior/:email", async (req, res) => {
  try {
    const { email } = req.params;

    const user = await User.findOne({ email });

    if (!user) {
      return res.status(400).json({ message: "User not found" });
    }

    const behaviors = await BehaviorCamera.find({ email });

    res.status(200).json(behaviors);
  } catch (error) {
    console.error(error);
    res.status(500).json({
      error: "An error occurred while retrieving behavior information",
    });
  }
});

export default router;
