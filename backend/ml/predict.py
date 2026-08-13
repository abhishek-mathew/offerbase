import argparse
import json
from pathlib import Path

import joblib


MODEL_PATH = Path("model/email_classifier.joblib")


def main():
    parser = argparse.ArgumentParser()

    parser.add_argument("--subject", default="")
    parser.add_argument("--sender", default="")
    parser.add_argument("--body", default="")

    args = parser.parse_args()

    model = joblib.load(MODEL_PATH)

    text = f"{args.subject} {args.sender} {args.body}"

    probabilities = model.predict_proba([text])[0]
    classes = model.classes_

    best_index = probabilities.argmax()

    result = {
        "label": str(classes[best_index]),
        "confidence": float(probabilities[best_index]),
        "probabilities": {
            str(label): float(probability)
            for label, probability in zip(classes, probabilities)
        },
    }

    print(json.dumps(result, indent=2))


if __name__ == "__main__":
    main()