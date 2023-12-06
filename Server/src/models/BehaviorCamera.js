import mongoose from "mongoose";

const behaviorCameraSchema = new mongoose.Schema({
  email: {
    type: String,
    required: true,
  },
  image: {
    type: String,
    required: true,
  },
  behavior: {
    type: String,
    required: true,
  },
  questions: [
    {
      question: {
        type: String,
        required: true,
      },
      answer: {
        type: String,
        required: true,
      },
    },
  ],
  createdAt: {
    type: Date,
    default: Date.now,
  },
});

const BehaviorCamera = mongoose.model("BehaviorCamera", behaviorCameraSchema);

export default BehaviorCamera;
