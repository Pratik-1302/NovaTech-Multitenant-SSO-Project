# Quick Start - Deploy to Render

The fastest way to get NovaTech SSO running in production.

## Prerequisites
- GitHub account
- Render account (free: [render.com/signup](https://render.com/signup))
- Domain: pratiktech.cloud (with DNS access)

## 1. Push to GitHub (5 minutes)

```bash
cd "c:\Project Backuppppppps\Fully Functional - ready to deploy\NovaTech"
git init
git add .
git commit -m "Production-ready NovaTech SSO"
git remote add origin https://github.com/YOUR_USERNAME/novatech-sso.git
git push -u origin main
```

## 2. Deploy on Render (2 clicks)

1. Log in to [Render Dashboard](https://dashboard.render.com)
2. Click **"+ New"** → **"Blueprint"**
3. Connect your GitHub repository (`novatech-sso`)
4. Click **"Connect"**

Render automatically:
- ✅ Creates PostgreSQL database
- ✅ Builds application (Java 21)
- ✅ Deploys to production
- ✅ Provides HTTPS URL

**Wait 10-15 minutes for first build.**

## 3. Set Up Custom Domain (5 minutes)

In Render service settings:
1. Go to **"Settings"** → **"Custom Domains"**
2. Add: `pratiktech.cloud`
3. Add: `www.pratiktech.cloud` (optional)

In your domain registrar (GoDaddy, Namecheap, etc.):
```
Type: A
Name: @
Value: 216.24.57.1
TTL: 3600
```

**Wait 5-60 minutes for DNS propagation.**

## 4. Create Super Admin (3 minutes)

1. Visit: `https://pratiktech.cloud/signup`
2. Create account with your email
3. Connect to database via Render dashboard
4. Run SQL:
   ```sql
   UPDATE users
   SET role = 'ROLE_SUPER_ADMIN'
   WHERE email = 'your-email@domain.com';
   ```

## 5. Done! 🎉

Your SSO system is live at:
- 🌐 https://pratiktech.cloud
- 🔒 SSL certificate (automatic)
- 🗄️ PostgreSQL database (managed)

**Next**: Configure SSO providers (OIDC, SAML, JWT) via admin dashboard.

---

**Need Help?** See [RENDER_DEPLOYMENT.md](RENDER_DEPLOYMENT.md) for detailed guide.
