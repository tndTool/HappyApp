import express from "express";
import Sensor from "../models/Sensor.js";
import User from "../models/User.js";

const router = express.Router();

router.post("/data", async (req, res) => {
  try {
    const { email, values } = req.body;

    const user = await User.findOne({ email });

    if (!user) {
      return res.status(400).json({ message: "User not found" });
    }

    if (!user.isVerified) {
      return res.status(400).json({ error: "User not found." });
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

export default router;
