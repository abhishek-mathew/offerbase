import pandas as pd

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline
from sklearn.metrics import classification_report, confusion_matrix


TRAIN_PATH = "data/training_emails.csv"
CHALLENGE_PATH = "data/challenge_emails.csv"


def combine_text(df):
    return (
        df["subject"].fillna("")
        + " "
        + df["sender"].fillna("")
        + " "
        + df["body"].fillna("")
    )


def main():
    train_df = pd.read_csv(TRAIN_PATH)
    challenge_df = pd.read_csv(CHALLENGE_PATH)

    X_train = combine_text(train_df)
    y_train = train_df["label"]

    X_challenge = combine_text(challenge_df)
    y_challenge = challenge_df["label"]

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

    predictions = model.predict(X_challenge)

    print("\nCHALLENGE SET RESULTS\n")

    print(
        classification_report(
            y_challenge,
            predictions,
            zero_division=0,
        )
    )

    print("Confusion matrix:\n")

    labels = [
        "APPLIED",
        "INTERVIEW",
        "OFFER",
        "REJECTED",
        "OTHER",
    ]

    print(
        confusion_matrix(
            y_challenge,
            predictions,
            labels=labels,
        )
    )

    print("\nMISCLASSIFIED EMAILS\n")

    for index, prediction in enumerate(predictions):

        actual = y_challenge.iloc[index]

        if prediction != actual:

            row = challenge_df.iloc[index]

            print(
                f"SUBJECT: {row['subject']}"
            )

            print(
                f"ACTUAL: {actual}"
            )

            print(
                f"PREDICTED: {prediction}"
            )

            print(
                f"BODY: {row['body']}"
            )

            print("-" * 60)


if __name__ == "__main__":
    main()