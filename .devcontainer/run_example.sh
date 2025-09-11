#!/bin/bash
set -e

# Check for required secrets
if [ -z "${ADYEN_API_KEY}" ] || [ -z "${ADYEN_MERCHANT_ACCOUNT}" ] || [ -z "${ADYEN_CLIENT_KEY}" ] || [ -z "${ADYEN_HMAC_KEY}" ]; then
  echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  echo "!!! ERROR: Required Adyen secrets are not set in your Codespaces secrets.  !!!"
  echo "!!!                                                                        !!!"
  echo "!!! Please go to your repository settings > Secrets and variables >        !!!"
  echo "!!! Codespaces, and add the required secrets.                              !!!"
  echo "!!!                                                                        !!!"
  echo "!!! After adding the secrets, you will need to rebuild the container.      !!!"
  echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  exit 1
fi

# Default to checkout-example if not set
EXAMPLE_DIR=${1:-checkout-example}

echo "---"
echo "Attempting to start Adyen example: ${EXAMPLE_DIR}"
echo "---"

# Navigate to the example directory and run the application
cd "${EXAMPLE_DIR}" && ./gradlew bootRun
