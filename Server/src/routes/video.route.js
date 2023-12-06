import express from "express";
import BehaviorVideo from "../models/BehaviorVideo.js";
import { uploadVideo } from "../configs/cloudinary.config.js";
import User from "../models/User.js";

const router = express.Router();

router.post("/behavior", uploadVideo.single("video"), async (req, res) => {
  try {
    const { email, behavior, questions } = req.body;
    const video = req.file.path;

    const user = await User.findOne({ email });

    if (!user) {
      return res.status(400).json({ message: "User not found" });
    }

    if (!user.isVerified) {
      return res.status(400).json({ error: "User not found." });
    }

    const newBehaviorVideo = new BehaviorVideo({
      email,
      video,
      behavior,
      questions,
    });

    const savedBehaviorVideo = await newBehaviorVideo.save();

    res.status(201).json(savedBehaviorVideo);
  } catch (error) {
    console.error(error);
    res
      .status(500)
      .json({ error: "An error occurred while creating the behavior object" });
  }
});

export default router;
