# Deployment Guide for TeenPay

## Deploy to Railway

### Prerequisites
- A GitHub account
- A Railway account (sign up at https://railway.app)

### Steps

1. **Push to GitHub**
   ```bash
   cd simple-java-version
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin <your-github-repo-url>
   git push -u origin main
   ```

2. **Deploy on Railway**
   - Go to https://railway.app
   - Click "New Project"
   - Select "Deploy from GitHub repo"
   - Choose your repository
   - Railway will automatically detect the Java app and deploy it
   - Your app will be live at a URL like: `https://your-app.up.railway.app`

### Files Needed for Deployment

The following files are required and have been created:
- ✅ `src/` - All Java source files
- ✅ `public/` - HTML, CSS files
- ✅ `Procfile` - Tells Railway how to start the app
- ✅ `railway.toml` - Railway configuration
- ✅ `nixpacks.toml` - Build configuration

### Environment Variables

No environment variables are required for basic deployment. The app uses an in-memory database.

### Important Notes

⚠️ **Database Limitation**: The app uses an in-memory database, which means:
- Data will be lost when the app restarts
- Each deployment will reset the database
- For production, you'd need to integrate a real database (PostgreSQL, MySQL, etc.)

### Alternative: Deploy to Render

If you prefer Render:
1. Go to https://render.com
2. Create a new "Web Service"
3. Connect your GitHub repo
4. Use these settings:
   - **Build Command**: `javac -d out src/com/teenupi/*.java src/com/teenupi/model/*.java src/com/teenupi/service/*.java`
   - **Start Command**: `java -cp out com.teenupi.TeenPayApp`
   - **Environment**: Java

### Testing Locally

Before deploying, test with the PORT environment variable:
```bash
# Windows
set PORT=3000 && java -cp out com.teenupi.TeenPayApp

# Linux/Mac
PORT=3000 java -cp out com.teenupi.TeenPayApp
```
