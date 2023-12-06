import express from "express";
import authenRouter from "./authen.route.js";
import userRouter from "./user.route.js";
import sensorRouter from "./sensor.route.js";
import behaviorImageRouter from "./camera.route.js";
import behaviorVideoRouter from "./video.route.js";

const router = express.Router();

router.use("/", authenRouter);
router.use("/user", userRouter);
router.use("/sensor", sensorRouter);
router.use("/camera", behaviorImageRouter);
router.use("/video", behaviorVideoRouter);

export default router;
