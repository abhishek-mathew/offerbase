import pandas as pd
import joblib

from sklearn.metrics import classification_report, confusion_matrix


MODEL_PATH = "model/email_classifier.joblib"
HOLDOUT_PATH = "data/holdout_emails.csv"


def combine_text(df):
    return (
        df["subject"].fillna("")
        + " "
        + df["sender"].fillna("")
        + " "
        + df["body"].fillna("")
    )


def main():
    model = joblib.load(MODEL_PATH)

    df = pd.read_csv(HOLDOUT_PATH)

    X = combine_text(df)
    y = df["label"]

    predictions = model.predict(X)

    print("\nHOLDOUT RESULTS\n")

    print(
        classification_report(
            y,
            predictions,
            zero_division=0,
        )
    )

    labels = [
        "APPLIED",
        "INTERVIEW",
        "OFFER",
        "REJECTED",
        "OTHER",
    ]

    print("\nCONFUSION MATRIX\n")

    print(
        confusion_matrix(
            y,
            predictions,
            labels=labels,
        )
    )

    print("\nMISCLASSIFIED EMAILS\n")

    for index, prediction in enumerate(predictions):

        actual = y.iloc[index]

        if prediction != actual:

            row = df.iloc[index]

            print(f"SUBJECT: {row['subject']}")
            print(f"ACTUAL: {actual}")
            print(f"PREDICTED: {prediction}")
            print(f"BODY: {row['body']}")
            print("-" * 60)


if __name__ == "__main__":
    main()