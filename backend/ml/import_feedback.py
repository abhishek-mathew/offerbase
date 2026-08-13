import argparse
import csv
from pathlib import Path

import requests


OUTPUT_PATH = Path(
    "data/feedback_emails.csv"
)


def main():
    parser = argparse.ArgumentParser()

    parser.add_argument(
        "--token",
        required=True,
    )

    args = parser.parse_args()

    response = requests.get(
        "http://localhost:8080/api/gmail/emails/feedback",
        headers={
            "Authorization":
                f"Bearer {args.token}"
        },
        timeout=30,
    )

    response.raise_for_status()

    feedback = response.json()

    OUTPUT_PATH.parent.mkdir(
        parents=True,
        exist_ok=True,
    )

    with OUTPUT_PATH.open(
        "w",
        newline="",
        encoding="utf-8",
    ) as file:

        writer = csv.writer(
            file
        )

        writer.writerow(
            [
                "subject",
                "sender",
                "body",
                "label",
            ]
        )

        for item in feedback:

            writer.writerow(
                [
                    item.get(
                        "subject",
                        "",
                    ),

                    item.get(
                        "sender",
                        "",
                    ),

                    item.get(
                        "body",
                        "",
                    ),

                    item[
                        "actualLabel"
                    ],
                ]
            )

    print(
        f"Imported {len(feedback)} reviewed emails"
    )

    print(
        f"Saved to {OUTPUT_PATH}"
    )


if __name__ == "__main__":
    main()