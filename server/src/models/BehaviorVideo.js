import mongoose from "mongoose";

const behaviorVideoSchema = new mongoose.Schema({
  email: {
    type: String,
    required: true,
  },
  video: {
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

const BehaviorVideo = mongoose.model("BehaviorVideo", behaviorVideoSchema);

export default BehaviorVideo;
