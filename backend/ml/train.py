import pandas as pd

from pathlib import Path
import joblib

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import train_test_split
from sklearn.pipeline import Pipeline
from sklearn.metrics import classification_report, confusion_matrix


DATA_PATH = "data/training_emails.csv"
FEEDBACK_PATH = "data/feedback_emails.csv"
MODEL_PATH = Path("model/email_classifier.joblib")


def main():
    df = pd.read_csv(DATA_PATH)

    feedback_path = Path(FEEDBACK_PATH)

    if feedback_path.exists():
        feedback_df = pd.read_csv(feedback_path)

        if not feedback_df.empty:
            df = pd.concat(
                [df, feedback_df],
                ignore_index=True,
            )

            print(
                f"Added {len(feedback_df)} real reviewed emails"
            )

    df["text"] = (
        df["subject"].fillna("")
        + " "
        + df["sender"].fillna("")
        + " "
        + df["body"].fillna("")
    )

    X_train, X_test, y_train, y_test = train_test_split(
        df["text"],
        df["label"],
        test_size=0.35,
        random_state=42,
        stratify=df["label"],
    )

    model = Pipeline(
        [
            (
                "tfidf",
                TfidfVectorizer(
                    ngram_range=(1, 2),
                    lowercase=True,
                    min_df=1,
                ),
            ),
            (
                "classifier",
                LogisticRegression(
                    max_iter=1000,
                ),
            ),
        ]
    )

    model.fit(X_train, y_train)

    predictions = model.predict(X_test)

    print("\nClassification report:\n")
    print(
        classification_report(
            y_test,
            predictions,
            zero_division=0,
        )
    )

    print("\nConfusion matrix:\n")
    print(confusion_matrix(y_test, predictions))

    MODEL_PATH.parent.mkdir(
        parents=True,
        exist_ok=True,
    )

    joblib.dump(
        model,
        MODEL_PATH,
    )

    print(
        f"\nSaved trained model to {MODEL_PATH}"
    )


if __name__ == "__main__":
    main()