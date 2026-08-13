import csv
import random
from pathlib import Path


OUTPUT_PATH = Path("data/training_emails.csv")

random.seed(42)


COMPANIES = [
    "Capital One",
    "Amazon",
    "Microsoft",
    "Google",
    "Meta",
    "Stripe",
    "Palantir",
    "Netflix",
    "Adobe",
    "Salesforce",
    "Uber",
    "Airbnb",
    "Bloomberg",
    "Datadog",
    "Cloudflare",
]

POSITIONS = [
    "Software Engineer",
    "Software Engineering Intern",
    "Backend Engineer",
    "Frontend Engineer",
    "Full Stack Engineer",
    "Data Analyst",
    "Data Scientist",
    "Machine Learning Engineer",
    "Product Analyst",
    "Systems Engineer",
]


def sender(company):
    styles = [
        f"{company} Recruiting <recruiting@{domain(company)}>",
        f"{company} Careers <careers@{domain(company)}>",
        f"{company} Talent Acquisition <jobs@{domain(company)}>",
        f"no-reply@{domain(company)}",
    ]

    return random.choice(styles)


def domain(company):
    return (
        company.lower()
        .replace(" ", "")
        .replace("&", "")
        + ".com"
    )


def applied_example():
    company = random.choice(COMPANIES)
    position = random.choice(POSITIONS)

    subjects = [
        f"Thank you for applying to {company}",
        f"Application received - {position}",
        f"Your application has been submitted",
        f"{company} application confirmation",
        f"We received your application",
    ]

    bodies = [
        f"We have received your application for the {position} position.",
        f"Thank you for applying for the {position} role at {company}.",
        f"Your application for {position} was successfully submitted.",
        f"We appreciate your interest in {company}. Your application is now under review.",
        f"This message confirms that we received your application for {position}.",
        f"Your materials have been received and will be reviewed by our recruiting team.",
        f"Your submission for {position} is complete and no further action is required.",
        f"We have successfully received your candidate profile for consideration.",
        f"Your application is currently being reviewed. We will contact you if your experience matches our needs.",
        f"Thank you for your interest in {company}. Your information has been submitted to the hiring team.",
    ]

    return (
        random.choice(subjects),
        sender(company),
        random.choice(bodies),
        "APPLIED",
    )


def interview_example():
    company = random.choice(COMPANIES)
    position = random.choice(POSITIONS)

    subjects = [
        f"Interview invitation - {position}",
        f"Next steps for your application",
        f"{company} interview scheduling",
        f"Technical interview invitation",
        f"Phone screen availability",
    ]

    bodies = [
        f"We would like to schedule an interview for the {position} position.",
        f"Please send us your availability for a conversation with the hiring team.",
        f"We would like to invite you to the next round of interviews.",
        f"Your application has progressed and we would like to schedule a technical interview.",
        f"Please select a time for your phone screen regarding the {position} role.",
        f"The hiring manager would like to speak with you about your experience.",
        f"We enjoyed reviewing your background and would like to continue the process with a conversation.",
        f"Please use the scheduling link below to select a time to meet with our engineering team.",
        f"We would like you to speak with several members of the team as the next step.",
        f"Your candidacy is moving forward and we'd like to arrange a conversation with the hiring manager.",
    ]

    return (
        random.choice(subjects),
        sender(company),
        random.choice(bodies),
        "INTERVIEW",
    )


def offer_example():
    company = random.choice(COMPANIES)
    position = random.choice(POSITIONS)

    subjects = [
        f"Offer Letter - {company}",
        f"Employment offer",
        f"Congratulations from {company}",
        f"Your offer for {position}",
        f"Offer details",
    ]

    bodies = [
        f"We are pleased to offer you the {position} position at {company}.",
        f"We are excited to extend an offer for the {position} role.",
        f"Congratulations. We would like to offer you a position on our team.",
        f"Attached are the details of your employment offer for {position}.",
        f"We are happy to offer you the opportunity to join {company}.",
        f"We would be thrilled to have you join our team as a {position}.",
        f"Your compensation package and proposed start date are included for your review.",
        f"We are excited to invite you to join {company}. Please review the attached compensation and benefits information.",
        f"Congratulations on completing our interview process. We would like you to join us as a {position}.",
        f"The team was impressed with you throughout the process and we are excited to welcome you to {company}.",
    ]

    return (
        random.choice(subjects),
        sender(company),
        random.choice(bodies),
        "OFFER",
    )


def rejected_example():
    company = random.choice(COMPANIES)
    position = random.choice(POSITIONS)

    subjects = [
        f"Application update",
        f"Regarding your application",
        f"{company} hiring update",
        f"Status of your application",
        f"Update for {position}",
    ]

    bodies = [
        f"Unfortunately, we have decided not to move forward with your application.",
        f"After careful consideration, you were not selected for the {position} role.",
        f"We will not be moving forward with your candidacy at this time.",
        f"We have chosen to proceed with other candidates.",
        f"Thank you for your interest in {company}, but we are unable to offer you the position.",
        f"While we were impressed by your experience, we have decided to pursue other applicants.",
        f"We enjoyed learning about your background, but we will not be advancing your candidacy.",
        f"We have selected candidates whose qualifications more closely match our current needs.",
        f"Although your background is impressive, we have decided to continue the process with other candidates.",
        f"At this time we are closing your candidacy for the {position} opportunity.",
    ]

    return (
        random.choice(subjects),
        sender(company),
        random.choice(bodies),
        "REJECTED",
    )


def other_example():
    company = random.choice(COMPANIES)

    examples = [
        (
            f"Explore careers at {company}",
            f"{company} Careers <careers@{domain(company)}>",
            "Learn more about our open roles and engineering teams.",
        ),
        (
            "Weekly job recommendations",
            "LinkedIn Jobs <jobs-noreply@linkedin.com>",
            "Here are new jobs that match your profile.",
        ),
        (
            "Your package has shipped",
            "Amazon <shipment-tracking@amazon.com>",
            "Your package will arrive tomorrow.",
        ),
        (
            "Password reset request",
            "GitHub <noreply@github.com>",
            "Use this link to reset your password.",
        ),
        (
            "Upcoming career fair",
            "University Career Center <careercenter@example.edu>",
            "Join us for this week's employer networking event.",
        ),
        (
            "New job alert",
            "Indeed <alerts@indeed.com>",
            "Several new software engineering roles were posted today.",
        ),
        (
            "Welcome to our talent community",
            f"{company} Talent Network <talent@{domain(company)}>",
            "Thanks for joining our talent network. We will share future opportunities.",
        ),

        # Real-world style negatives

        (
            "New jobs: Software Engineer Intern and 7 more jobs",
            "Wellfound <team@hi.wellfound.com>",
            "I've found 8 new jobs that might interest you. Ready to Interview Open to offers Closed to Offers.",
        ),
        (
            "New jobs: Senior Fullstack Engineer and 6 more jobs",
            "Wellfound <team@hi.wellfound.com>",
            "Here are several new opportunities that match your profile. Ready to Interview and open to offers.",
        ),
        (
            "I countered a job offer today",
            "Glassdoor Community <noreply@glassdoor.com>",
            "Explore real conversations from workers discussing job offers, interviews and compensation.",
        ),
        (
            "Interview advice from the Glassdoor community",
            "Glassdoor Community <noreply@glassdoor.com>",
            "Read what other candidates say about interview experiences at popular companies.",
        ),
        (
            "We Received Your FAFSA Form",
            "U.S. Department of Education <donotreply@studentaid.gov>",
            "Log in to your account to see your status and next steps.",
        ),
        (
            "Your financial aid application was received",
            "Student Aid <donotreply@studentaid.gov>",
            "Your application has been received. Review your account for next steps.",
        ),
        (
            "Your Parking Receipt",
            "donotreply@parking.com",
            "Thank you for purchasing parking using our application. Below are your parking session details.",
        ),
        (
            "A third-party OAuth application was added",
            "GitHub <noreply@github.com>",
            "An OAuth application was recently authorized to access your account.",
        ),
        (
            "Career Insights Newsletter",
            f"{company} Talent Acquisition <careers@{domain(company)}>",
            "Thank you for being part of our talent community. Read employee stories and company news.",
        ),
        (
            "Stay Connected for Future Career Opportunities",
            f"{company} Careers <careers@{domain(company)}>",
            "We would like to keep your information on file for future opportunities.",
        ),
        (
            "Recruiter recently posted",
            "LinkedIn <updates-noreply@linkedin.com>",
            "A recruiter you follow recently posted about an open engineering role.",
        ),
    ]

    subject, sender_value, body = random.choice(examples)

    return (
        subject,
        sender_value,
        body,
        "OTHER",
    )


GENERATORS = {
    "APPLIED": applied_example,
    "INTERVIEW": interview_example,
    "OFFER": offer_example,
    "REJECTED": rejected_example,
    "OTHER": other_example,
}


def main():
    rows = []

    examples_per_class = 60

    for label, generator in GENERATORS.items():

        count = 120 if label == "OTHER" else 60

        for _ in range(count):
            rows.append(
                generator()
            )

    random.shuffle(rows)

    OUTPUT_PATH.parent.mkdir(
        parents=True,
        exist_ok=True,
    )

    with OUTPUT_PATH.open(
        "w",
        newline="",
        encoding="utf-8",
    ) as file:

        writer = csv.writer(file)

        writer.writerow(
            [
                "subject",
                "sender",
                "body",
                "label",
            ]
        )

        writer.writerows(rows)

    print(
        f"Generated {len(rows)} training emails"
    )

    print(
        f"Saved to {OUTPUT_PATH}"
    )


if __name__ == "__main__":
    main()