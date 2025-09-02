# S3 Demo
Amazon S3 (Simple Storage Service) buckets are versatile cloud storage containers used in a variety of applications. 
Below are use cases of S3 bucket for this demo:
- Content Delivery Network (CDN) Integration
- Media Uploads for a Web or Mobile Application

## Environment Variables
The following environment variables will need to be configured in `application.yml` read the `Digital Ocean Setup` steps below
to get the environment variables.

- S3_BUCKET_NAME
- S3_CDN_ENDPOINT
- S3_ENDPOINT
- S3_REGION
- S3_ACCESS_KEY
- S3_SECRET_KEY

## Digital Ocean Setup
### Digital Ocean Setup: Creating an S3-Compatible Bucket

1. **Go to DigitalOcean**: [DigitalOcean's website](https://www.digitalocean.com/) and log in or create an account if you don't already have one.

2. **Access the Control Panel**: Once logged in, navigate to the **DigitalOcean Control Panel**.

3. **Navigate to Spaces**:
    - From the left-hand menu, click on **"Spaces"** under the **Storage** section.
    - If you don't see "Spaces," ensure that your account is eligible for Spaces usage (part of the basic DigitalOcean services).

4. **Create a New Space**:
    - Click **"Create a Space"**.
    - Select a **datacenter region** close to your application or users for optimal performance.
    - Enter a unique name for your Space (e.g., `my-app-assets`).
    - Choose the default permissions:
        - **Restrict file listing (recommended)**: Keeps your bucket contents private by default.
        - You can make individual files or the entire bucket public if needed later.

5. **Configure Your Space**:
    - Set the permissions:
        - If you plan to serve static files (e.g., images), you can make the bucket public.
        - If using the bucket for private storage, keep it private and use pre-signed URLs for access.
    - Decide whether to enable CDN:
        - Enabling the **CDN option** allows faster global access to your files by caching them on edge servers.
    - Click **Create Space**.

6. **Generate Access Keys**:
    - Navigate to **API** in the left-hand menu.
    - Click **Generate New Key** under the "Spaces access keys" section.
    - Note down the **Access Key** and **Secret Key**. You will need these to connect your application to the bucket. *These keys are only displayed once.*

7. **Test Your Setup**:
    - Use tools like **Cyberduck** or the AWS CLI (configured with your DigitalOcean Space credentials) to connect and test uploading/downloading files.
    - Example AWS CLI setup for Spaces:
      ```bash
      aws configure
      Access Key ID: [Your DigitalOcean Access Key]
      Secret Access Key: [Your DigitalOcean Secret Key]
      Default region: [Region of your Space, e.g., nyc3]
      Default output format: json
      ```

8. **Integrate with Your Application**:
    - Use AWS SDKs or libraries to integrate DigitalOcean Spaces with your app.
    - Replace S3 endpoints with `https://[region].digitaloceanspaces.com` (e.g., `https://nyc3.digitaloceanspaces.com`).
