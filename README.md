# payment-service

Go to https://dashboard.stripe.com/webhooks

Add endpoint:
http://localhost:8083/api/payment/webhook

Select event: checkout.session.completed

Copy the webhook secret and paste it in application.yml under stripe.webhook.secret

