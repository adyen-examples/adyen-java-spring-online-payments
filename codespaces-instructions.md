# Setting up Adyen examples with GitHub Codespaces

This guide will walk you through the steps to run the Adyen Java Spring Online Payments examples in a GitHub Codespaces environment.

## Step 1: Adyen Account Setup

Before launching the Codespace, you need to configure a few things in your Adyen Customer Area.

1.  **API Credentials:**
    *   If you don't have one, create a set of [API credentials](https://docs.adyen.com/development-resources/api-credentials/) for your test environment.
    *   You will need the following values:
        *   **API Key**
        *   **Client Key**
        *   **Merchant Account**

2.  **Allowed Origins:**
    *   To allow the Adyen UI components to load in the Codespace, you need to add the Codespace URL to your list of allowed origins.
    *   Navigate to your API credentials, and in the **Client settings**, add the following URL to the **Allowed origins** list:
        ```
        https://*.github.dev
        ```

3.  **Webhook HMAC Key:**
    *   In your Customer Area, go to **Developers > Webhooks**. 
    *   Create a new **Standard webhook**.
    *   Generate an **HMAC Key**. You will need this for the next step.

## Step 2: Configure GitHub Codespaces Secrets

To securely use your Adyen credentials in the Codespace, you should set them as repository secrets. The dev container is configured to automatically pick them up.

1.  Navigate to your forked repository on GitHub and go to **Settings > Secrets and variables > Codespaces**.
2.  Create the following secrets:
    *   `ADYEN_API_KEY`: Your Adyen API Key.
    *   `ADYEN_CLIENT_KEY`: Your Adyen Client Key.
    *   `ADYEN_MERCHANT_ACCOUNT`: Your Adyen Merchant Account.
    *   `ADYEN_HMAC_KEY`: The HMAC key you generated during webhook setup.

## Step 3: Launching the Codespace

1.  Navigate to the `README.md` file of the example you want to run (e.g., `checkout-example/README.md`).
2.  Click the **Open in GitHub Codespaces** badge.
3.  This will create and launch a new Codespace. The project will be built automatically, and the application will start.

## Step 4: Configure the Webhook URL

Once the Codespace is running, you need to update your Adyen webhook with the public URL of the running application.

1.  In the Codespace, a terminal will open, and the application will be running on port 8080.
2.  Go to the **Ports** tab in the VS Code interface within your Codespace.
3.  Find the entry for port 8080 and copy the **Forwarded Address** (it will look something like `https://my-codespace-name-8080.preview.app.github.dev`).
4.  Go back to your Adyen Customer Area and navigate to **Developers > Webhooks**.
5.  Select the webhook you created earlier and paste the copied URL into the **Server configuration** URL field, appending the webhook endpoint:
    ```
    <your_forwarded_address>/api/webhooks/notifications
    ```
    For example:
    ```
    https://my-codespace-name-8080.preview.app.github.dev/api/webhooks/notifications
    ```
6.  Ensure the webhook is **enabled** and save your changes.

You are now ready to test the integration within GitHub Codespaces!
