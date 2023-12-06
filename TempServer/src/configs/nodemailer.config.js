import nodemailer from "nodemailer";

const transporter = nodemailer.createTransport({
  service: "gmail",
  host: ["smtp.gmail.com", "smtp.mailinator.com", "smtp.yopmail.com"],
  port: 587,
  secure: false,
  auth: {
    user: process.env.USER_NODEMAILER,
    pass: process.env.PASS_NODEMAILER,
  },
});

export default transporter;
