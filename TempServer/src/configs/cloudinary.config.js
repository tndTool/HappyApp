import { v2 as cloudinary } from "cloudinary";
import { CloudinaryStorage } from "multer-storage-cloudinary";
import multer from "multer";

cloudinary.config({
  cloud_name: process.env.CLOUDINARY_NAME,
  api_key: process.env.CLOUDINARY_KEY,
  api_secret: process.env.CLOUDINARY_SECRET,
});

const imageStorage = new CloudinaryStorage({
  cloudinary,
  params: {
    folder: "behavior-images",
    allowedFormats: ["jpg", "png"],
  },
});

const videoStorage = new CloudinaryStorage({
  cloudinary,
  params: {
    folder: "behavior-videos",
    resource_type: "video",
    allowed_formats: ["mp4", "mov"],
  },
});

const uploadImage = multer({ storage: imageStorage });
const uploadVideo = multer({ storage: videoStorage });

export { uploadImage, uploadVideo };
