# Render Deployment Guide

## Quick Deploy to Render

### Option 1: Using GitHub (Recommended)

1. **Create a GitHub account** (if you don't have one)
   - Go to https://github.com
   - Click "Sign up"
   - Use your email: dev.bhuyan256@gmail.com

2. **Create a new repository**
   - Click the "+" icon → "New repository"
   - Name it: `teenpay`
   - Make it Public
   - Click "Create repository"

3. **Upload your code**
   - On the repository page, click "uploading an existing file"
   - Drag and drop your entire `simple-java-version` folder
   - Click "Commit changes"

4. **Deploy on Render**
   - Go to https://render.com
   - Sign up with GitHub
   - Click "New +" → "Web Service"
   - Select your `teenpay` repository
   - Settings:
     - **Name**: teenpay
     - **Language**: Docker
     - **Region**: Singapore (or closest to you)
     - **Branch**: main
     - **Root Directory**: (leave empty)
     - **Runtime**: Docker
     - **Instance Type**: Free

   - Click "Create Web Service"

   > **Note**: Since we added a `Dockerfile`, Render will automatically detect it and know how to build your app. You don't need to enter any build commands manually!

### Option 2: Using Render Blueprint (Easier)

1. Create a `render.yaml` file (already created for you)
2. Push to GitHub
3. Deploy using Render Blueprint

Your app will be live at: `https://teenpay.onrender.com`

## Free Tier Limits

- ✅ Free forever
- ⚠️ App sleeps after 15 minutes of inactivity
- ⚠️ Takes ~30 seconds to wake up on first request
- ✅ 750 hours/month free (enough for 24/7 if you upgrade to paid)
