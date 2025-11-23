# 🚀 Deploying NovaTech to Render with Custom Domain

Complete step-by-step guide to deploy **NovaTech Dynamic Multi-Protocol SSO Management System** to Render with PostgreSQL database and custom domain **pratiktech.cloud**.

---

## 📋 Prerequisites

Before you begin, ensure you have:

1. ✅ **Render Account**: Sign up at [render.com](https://render.com) (free tier available)
2. ✅ **GitHub Account**: Your code should be in a GitHub repository
3. ✅ **Custom Domain**: pratiktech.cloud (registered and accessible)
4. ✅ **Domain DNS Access**: Ability to modify DNS records for pratiktech.cloud

---

## 🎯 Deployment Overview

This deployment will create:
- 🌐 **Web Service**: NovaTech application (Java 21)
- 🗄️ **PostgreSQL Database**: Managed database on Render
- 🔒 **SSL Certificate**: Automatic HTTPS via Let's Encrypt
- 🌍 **Custom Domain**: pratiktech.cloud

**Estimated Time**: 15-20 minutes

---

## 📝 Step-by-Step Deployment Guide

### Step 1: Push Your Code to GitHub

1. **Initialize Git** (if not already done):
   ```bash
   cd "c:\Project Backuppppppps\Fully Functional - ready to deploy\NovaTech"
   git init
   git add .
   git commit -m "Production-ready NovaTech SSO system"
   ```

2. **Create GitHub Repository**:
   - Go to [github.com/new](https://github.com/new)
   - Name: `novatech-sso` (or any name you prefer)
   - Visibility: Private (recommended) or Public
   - Click "Create repository"

3. **Push to GitHub**:
   ```bash
   git remote add origin https://github.com/YOUR_USERNAME/novatech-sso.git
   git branch -M main
   git push -u origin main
   ```

---

### Step 2: Deploy to Render Using Blueprint

#### Option A: One-Click Deploy (Recommended)

1. **Log in to Render**: Go to [dashboard.render.com](https://dashboard.render.com)

2. **Create New Blueprint**:
   - Click "+ New" → "Blueprint"
   - Connect your GitHub account if not already connected
   - Select your repository (`novatech-sso`)
   - Click "Connect"

3. **Render will automatically**:
   - ✅ Detect `render.yaml`
   - ✅ Create PostgreSQL database (`novatech-db`)
   - ✅ Create web service (`novatech-sso`)
   - ✅ Link database to web service
   - ✅ Start building the application

4. **Monitor Build**:
   - Watch the build logs in real-time
   - Build takes ~5-10 minutes (downloads Maven dependencies)
   - Look for: `✅ Build successful!`

#### Option B: Manual Setup

If you prefer manual setup or encounter issues:

<details>
<summary>Click to expand manual setup instructions</summary>

**Step 2a: Create PostgreSQL Database**

1. In Render dashboard, click "+ New" → "PostgreSQL"
2. Configure:
   - **Name**: `novatech-db`
   - **Database**: `novatech_db`
   - **User**: `novatech_user`
   - **Region**: Oregon (or nearest to your users)
   - **Plan**: Starter ($7/month) or Free (limited)
3. Click "Create Database"
4. **Save the connection details** (you'll need them)

**Step 2b: Create Web Service**

1. Click "+ New" → "Web Service"
2. Connect your GitHub repository
3. Configure:
   - **Name**: `novatech-sso`
   - **Region**: Same as database (Oregon)
   - **Branch**: `main`
   - **Runtime**: Java
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -Dserver.port=$PORT -jar target/service-app-0.0.1-SNAPSHOT.jar`
4. Click "Advanced" and add environment variables (see Step 3)
5. Click "Create Web Service"

</details>

---

### Step 3: Configure Environment Variables

Render will auto-populate most variables, but verify these are set:

| Variable | Value | Notes |
|----------|-------|-------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Activates production configuration |
| `DATABASE_URL` | (auto-set by Render) | PostgreSQL connection string |
| `CUSTOM_DOMAIN` | `pratiktech.cloud` | Your custom domain |
| `ALLOWED_ORIGINS` | `https://pratiktech.cloud,https://www.pratiktech.cloud` | CORS configuration |
| `COOKIE_SECURE` | `true` | Enable secure cookies |
| `DB_POOL_SIZE` | `20` | Database connection pool size |
| `JAVA_OPTS` | `-Xmx512m -Xms256m` | Java memory settings |

**To add/edit environment variables**:
1. Go to your service → "Environment"
2. Click "Add Environment Variable"
3. Enter key and value
4. Click "Save Changes" (triggers redeploy)

---

### Step 4: Wait for First Deployment

1. **Monitor deployment**:
   - Go to "Events" tab to see deployment progress
   - Initial deploy: ~10-15 minutes
   - Subsequent deploys: ~5-8 minutes

2. **Check logs**:
   - Go to "Logs" tab
   - Look for: `Started NovaTechServiceAppApplication`
   - Verify no errors

3. **Test the deployment**:
   - Your app will be available at: `https://novatech-sso.onrender.com`
   - Visit the URL and verify the login page loads

---

### Step 5: Set Up Custom Domain (pratiktech.cloud)

#### 5a: Add Domain in Render

1. In your web service, go to **"Settings"** tab
2. Scroll to **"Custom Domains"** section
3. Click **"Add Custom Domain"**
4. Enter: `pratiktech.cloud`
5. Click **"Add"**
6. Repeat for: `www.pratiktech.cloud` (optional but recommended)

#### 5b: Configure DNS Records

Render will show you the DNS configuration needed. You'll need to add records in your domain registrar (e.g., GoDaddy, Namecheap, Cloudflare):

**For Root Domain (pratiktech.cloud)**:
```
Type: A
Name: @
Value: 216.24.57.1
TTL: 3600
```

**For WWW Subdomain** (optional):
```
Type: CNAME
Name: www
Value: novatech-sso.onrender.com
TTL: 3600
```

> **Note**: DNS propagation takes 5 minutes to 48 hours (usually within 1 hour)

#### 5c: Verify SSL Certificate

1. After DNS propagation, Render automatically provisions SSL certificate
2. Check "Custom Domains" section for **green checkmark** ✅
3. Visit `https://pratiktech.cloud` - you should see the 🔒 padlock

---

### Step 6: Create Super Admin Account

After successful deployment:

1. **Visit your app**: `https://pratiktech.cloud`

2. **Sign up**:
   - Click "Sign Up" or go to `/signup`
   - Enter your admin email (e.g., `admin@pratiktech.cloud`)
   - Enter password (strong password!)
   - Click "Register"

3. **Promote to Super Admin**:
   - Go to Render dashboard → Your database (`novatech-db`)
   - Click "Connect" → "External Connection"
   - Use a PostgreSQL client (pgAdmin, DBeaver, or `psql`)
   
   **Connection details from Render**:
   - Host: (provided by Render)
   - Port: (provided by Render)
   - Database: `novatech_db`
   - Username: (provided by Render)
   - Password: (provided by Render)

4. **Run SQL command**:
   ```sql
   UPDATE users
   SET role = 'ROLE_SUPER_ADMIN'
   WHERE email = 'admin@pratiktech.cloud';
   ```

5. **Verify**:
   - Log out and log back in
   - You should be redirected to `/superadmin/dashboard`

---

## ✅ Post-Deployment Checklist

After deployment, verify everything works:

- [ ] Application loads at `https://pratiktech.cloud` ✅
- [ ] SSL certificate is valid (green padlock) 🔒
- [ ] Login page displays correctly
- [ ] You can create an account
- [ ] Super Admin can access `/superadmin/dashboard`
- [ ] Database connection is stable (check logs for errors)
- [ ] SSO configuration page loads (`/admin/configure-saml`)
- [ ] No errors in Render logs

---

## 🔧 Configuration Management

### Managing SSO Providers

All SSO configurations are managed through the admin dashboard:

1. **Log in as Super Admin**
2. **Navigate to**: `/admin/configure-oidc`, `/admin/configure-saml`, or `/admin/configure-jwt`
3. **Enter provider details**:
   - Client ID
   - Client Secret
   - Authorization/Token/UserInfo endpoints
   - Certificates (if needed)
4. **Enable the provider**: Toggle "Enabled" switch
5. **Save**: Changes take effect immediately

### Uploading Certificates

For SAML/JWT providers that require certificates:

1. **Store certificates in `src/main/resources/`** (before deployment)
2. **Reference in admin dashboard**: `classpath:certificate_name.cer`
3. **Redeploy** if adding new certificates

**Alternative** (for production):
- Store certificates in environment variables as Base64-encoded strings
- Update code to read from environment variables

---

## 🐛 Troubleshooting

### Issue: Build Fails

**Symptoms**: Build logs show compilation errors

**Solutions**:
1. Check Java version: Render supports Java 17 and 21
2. Verify `pom.xml` has correct configuration
3. Check for missing dependencies
4. Review build logs for specific error

**Fix**:
```bash
# Locally test the build
./mvnw clean package -DskipTests
```

---

### Issue: Database Connection Failed

**Symptoms**: Logs show `Connection refused` or `Database unavailable`

**Solutions**:
1. Verify `DATABASE_URL` environment variable is set
2. Check database status in Render dashboard
3. Ensure database and web service are in same region
4. Verify connection pool settings

**Check logs for**:
```
HikariPool-1 - Exception during pool initialization
```

---

### Issue: Application Crashes on Startup

**Symptoms**: Application starts but immediately crashes

**Solutions**:
1. Check Java memory settings (`JAVA_OPTS`)
2. Verify `SPRING_PROFILES_ACTIVE=prod`
3. Review application logs for stack traces
4. Check for missing environment variables

**Increase memory** (if needed):
```
JAVA_OPTS=-Xmx768m -Xms384m
```

---

### Issue: Custom Domain Not Working

**Symptoms**: Domain doesn't resolve or shows SSL error

**Solutions**:
1. **Verify DNS records**: Use [dnschecker.org](https://dnschecker.org)
2. **Wait for propagation**: Can take up to 48 hours
3. **Check SSL status**: In Render → Custom Domains
4. **Verify CNAME/A record**: Must point to Render's servers

---

### Issue: Login Page Doesn't Load CSS

**Symptoms**: Page loads but looks broken (no styling)

**Solutions**:
1. Check browser console for 404 errors
2. Verify static resources are in `src/main/resources/static/`
3. Check Thymeleaf cache setting
4. Clear browser cache

---

## 🔐 Security Recommendations

### Before Going Live

1. **Change Default Passwords**:
   - Database password (Render generates secure ones)
   - Super Admin password (use strong password)

2. **Enable Database Backups**:
   - Upgrade to Standard plan ($7/month minimum)
   - Automatic daily backups included

3. **Set Up Monitoring**:
   - Enable Render health checks (automatic)
   - Set up uptime monitoring (UptimeRobot, Pingdom)

4. **Implement Rate Limiting**:
   - Add rate limiting to prevent brute-force attacks
   - Consider using Cloudflare for DDoS protection

5. **Regular Updates**:
   - Keep dependencies updated
   - Monitor security advisories
   - Update Java version as needed

---

## 📊 Monitoring & Logs

### Viewing Logs

1. **Real-time logs**:
   - Render Dashboard → Your Service → "Logs" tab
   - Shows last 1000 lines

2. **Persistent logs** (paid plans):
   - Integrate with logging services (Datadog, LogDNA)
   - Set up log retention

### Health Checks

Render automatically monitors your application:
- **Endpoint**: `/login` (public endpoint)
- **Interval**: Every 60 seconds
- **Failure threshold**: 3 consecutive failures trigger restart

**To change health check endpoint**:
- Edit `render.yaml`
- Set `healthCheckPath: /actuator/health` (if using Spring Actuator)

---

## 💰 Cost Estimate

**Render Pricing** (as of 2024):

| Resource | Plan | Cost |
|----------|------|------|
| Web Service | Starter | $7/month |
| PostgreSQL | Starter | $7/month |
| Custom Domain | Free | $0/month |
| SSL Certificate | Free | $0/month |
| **Total** | | **$14/month** |

**Free Tier Option**:
- Web Service: Free (spins down after inactivity)
- PostgreSQL: Free (limited, no backups)
- **Total**: $0/month (not recommended for production)

---

## 🚀 Scaling Your Application

As your user base grows:

### Vertical Scaling (Increase Resources)

1. **Upgrade Render Plan**:
   - Standard: 2GB RAM, $25/month
   - Pro: 4GB RAM, $85/month
   - Pro Plus: 8GB RAM, $175/month

2. **Upgrade Database**:
   - Standard: 16GB storage, daily backups, $7/month
   - Pro: 512GB storage, point-in-time recovery, $90/month

### Horizontal Scaling (Multiple Instances)

1. **Enable Auto-Scaling** (Enterprise plan):
   - Min instances: 2
   - Max instances: 10
   - Scale based on CPU/memory

### Performance Optimizations

1. **Enable Redis for Sessions**:
   - Add Redis service on Render
   - Configure Spring Session with Redis
   - Enables session persistence across restarts

2. **Add CDN**:
   - Use Cloudflare (free plan available)
   - Cache static assets (CSS, JS, images)
   - Reduces server load

---

## 🔄 Continuous Deployment

Your app is now configured for **automatic deployments**:

1. **Push to GitHub**:
   ```bash
   git add .
   git commit -m "Update feature"
   git push origin main
   ```

2. **Render automatically**:
   - Detects the push
   - Starts building
   - Runs tests (if configured)
   - Deploys to production
   - Updates `https://pratiktech.cloud`

3. **Rollback** (if needed):
   - Render Dashboard → "Events" tab
   - Find previous successful deploy
   - Click "Redeploy"

---

## 📞 Support & Resources

### Render Support
- **Documentation**: [docs.render.com](https://docs.render.com)
- **Community**: [community.render.com](https://community.render.com)
- **Status**: [status.render.com](https://status.render.com)

### NovaTech Support
- **GitHub Issues**: (your repository)/issues
- **Email**: admin@pratiktech.cloud

---

## 🎉 Success!

Your NovaTech Dynamic Multi-Protocol SSO Management System is now live at:

🌐 **https://pratiktech.cloud**

**Next Steps**:
1. Configure your SSO providers (OIDC, SAML, JWT)
2. Create user accounts
3. Test authentication flows
4. Monitor logs and performance

**Congratulations on your deployment!** 🚀
