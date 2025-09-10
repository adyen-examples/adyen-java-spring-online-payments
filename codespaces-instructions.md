# Setting up Adyen examples with GitHub Codespaces

This guide will walk you through the steps to run the Adyen Java Spring Online Payments examples in a GitHub Codespaces environment.

## Step 0: Choose Your Codespaces Setup

You have two options for setting up Codespaces with this repository:

### Option 1: Fork the Repository (Recommended for Personal Use)

Forking the repository allows you to manage your own Codespaces environment and secrets securely within your personal GitHub account.

1.  Navigate to the [repository page](https://github.com/adyen-examples/adyen-java-spring-online-payments).
2.  Click the **"Fork"** button in the top-right corner.
3.  Choose where you want to fork the repository (usually your personal account).

Once forked, you will be working from your own copy of the repository.

### Option 2: Use the Original Repository Directly (Secrets Configured in Your Account)

You can launch Codespaces directly from the original public repository without forking. In this case, you will configure the necessary Codespaces secrets within *your own GitHub account*, scoped to this repository.

**Important:** Ensure that the necessary Adyen secrets are configured in your *personal GitHub account's* Codespaces secrets, with the repository path (e.g. `adyen-examples/adyen-java-spring-online-payments`) as the scope.

## Step 2: Adyen Account Setup

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

## Step 3: Configure GitHub Codespaces Secrets

To securely use your Adyen credentials in the Codespace, you should set them as repository secrets. The dev container is configured to automatically pick them up.

1.  Depending on your chosen setup in Step 0:
    *   **If you forked the repository:** Navigate to your forked repository on GitHub and go to **Settings > Secrets and variables > Codespaces**.
    *   **If you are using the original repository directly:** Navigate to your *personal GitHub account* settings and go to **Settings > Codespaces > Secrets**. Create the secrets there, ensuring you set the **Repository access** to "Selected repositories" and add `adyen-examples/adyen-java-spring-online-payments` as a selected repository.
2.  Create the following secrets:
    *   `ADYEN_API_KEY`: Your Adyen API Key.
    *   `ADYEN_CLIENT_KEY`: Your Adyen Client Key.
    *   `ADYEN_MERCHANT_ACCOUNT`: Your Adyen Merchant Account.
    *   `ADYEN_HMAC_KEY`: The HMAC key you generated during webhook setup.

## Step 4: Launching the Codespace

1.  Navigate to the `README.md` file of the example you want to run (e.g., `checkout-example/README.md`).
2.  Click the **Open in GitHub Codespaces** badge.
3.  This will create and launch a new Codespace. The project will be built automatically, and the application will start.
4.  Use the Drop-in and try out payments on `TEST`

## Step 5: Configure the Webhook URL

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
7.  Make sure to change visibity of your port to `public` make the Webhook endpoint be reachable by Adyen


>   You are now ready to test the integration within GitHub Codespaces!


## Usage Tips

- **Updating Secrets**: After updating a secret, it's best to delete the existing Codespace and create a new one to ensure the changes are applied.
- **Port Forwarding**: Make sure port `8080` has its visibility set to **Public** in the `Ports` tab.
- **Testing Webhooks**: Use the **Webhook Test Configuration** option from your Customer Area to send test events directly to your Codespace.
- **Viewing Logs**: Monitor the application logs in the **Terminal** panel for debugging and status updates.
- **Managing Codespaces**: You can view and manage all your Codespaces at [github.com/codespaces](https://github.com/codespaces).
