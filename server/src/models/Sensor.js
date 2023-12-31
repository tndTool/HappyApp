import mongoose from "mongoose";

const sensorSchema = new mongoose.Schema({
  email: {
    type: String,
    required: true,
  },
  values: [
    {
      sensor: {
        type: String,
        required: true,
      },
      value: {
        type: String,
      },
    },
  ],
  createdAt: {
    type: Date,
    default: Date.now,
  },
});

const Sensor = mongoose.model("Sensor", sensorSchema);

export default Sensor;
