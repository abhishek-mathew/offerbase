from pathlib import Path

import joblib
from fastapi import FastAPI
from pydantic import BaseModel


MODEL_PATH = Path("model/email_classifier.joblib")

model = joblib.load(MODEL_PATH)

app = FastAPI(
    title="OfferBase ML Service",
    version="1.0.0",
)


class EmailRequest(BaseModel):
    subject: str = ""
    sender: str = ""
    body: str = ""


class PredictionResponse(BaseModel):
    label: str
    confidence: float
    probabilities: dict[str, float]


@app.get("/health")
def health():
    return {
        "status": "ok",
        "modelLoaded": True,
    }


@app.post(
    "/predict",
    response_model=PredictionResponse,
)
def predict_email(
    email: EmailRequest,
):
    text = (
        f"{email.subject} "
        f"{email.sender} "
        f"{email.body}"
    )

    probabilities = model.predict_proba(
        [text]
    )[0]

    classes = model.classes_

    best_index = probabilities.argmax()

    return {
        "label": str(
            classes[best_index]
        ),
        "confidence": float(
            probabilities[best_index]
        ),
        "probabilities": {
            str(label): float(probability)
            for label, probability
            in zip(
                classes,
                probabilities,
            )
        },
    }