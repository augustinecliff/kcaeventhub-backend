package tiketihub.emailconfig.templates;

import lombok.Data;
import lombok.Getter;

@Getter
public class EmailTemplates {
    private final String newUser = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "  <title>Welcome to TiketiHub</title>\n" +
            "  <style>\n" +
            "    /* Add your CSS styles here to make the email look appealing */\n" +
            "    body {\n" +
            "      font-family: Arial, sans-serif;\n" +
            "      line-height: 1.6;\n" +
            "    }\n" +
            "\n" +
            "    .header {\n" +
            "      background-color: #f1f1f1;\n" +
            "      padding: 20px;\n" +
            "      text-align: center;\n" +
            "    }\n" +
            "\n" +
            "    .header h1 {\n" +
            "      color: #007bff;\n" +
            "    }\n" +
            "\n" +
            "    .content {\n" +
            "      padding: 20px;\n" +
            "    }\n" +
            "\n" +
            "    .cta-btn {\n" +
            "      display: inline-block;\n" +
            "      background-color: #007bff;\n" +
            "      color: #fff;\n" +
            "      padding: 10px 20px;\n" +
            "      text-decoration: none;\n" +
            "      border-radius: 5px;\n" +
            "      margin-top: 20px;\n" +
            "    }\n" +
            "\n" +
            "    .cta-btn:hover {\n" +
            "      background-color: #0056b3;\n" +
            "    }\n" +
            "\n" +
            "    .footer {\n" +
            "      background-color: #f1f1f1;\n" +
            "      padding: 10px;\n" +
            "      text-align: center;\n" +
            "    }\n" +
            "  </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "<div class=\"header\">\n" +
            "  <h1>Congratulations for signing up for TiketiHub \uD83C\uDF89</h1>\n" +
            "  <p>Welcome to the neighborhood!\uD83C\uDF1F</p>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"content\">\n" +
            "  <p>We're delighted to have you aboard and can't wait for you to discover all the fascinating things we have to offer. We've got you covered whether you're here to network, study, or have fun.</p>\n" +
            "\n" +
            "  <p><strong>As a new user, we want your experience to be as pleasant as possible. Here are a few pointers to get you started:</strong></p>\n" +
            "\n" +
            "  <ul>\n" +
            "    <li>Fill Out Your Profile: Tell us a little bit about yourself! Including a profile picture and some information about your hobbies will allow you to connect with other individuals who share your interests.</li>\n" +
            "    <li>Examine Our Advantages: Take a tour of the app to see everything it has to offer. We created it with love and attention to meet your needs.</li>\n" +
            "    <li>Join Communities, Follow Intriguing Persons, and Begin Participating with Posts to Connect with Others. Our neighborhood is full of nice folks who are ready to meet you!</li>\n" +
            "    <li>Stay Informed: To get the most out of your app experience, keep an eye on our regular updates, announcements, and useful suggestions.</li>\n" +
            "    <li>Reach Out for Assistance: If you have any queries or face any problems, please contact our support staff. We're here to help you every step of the way.</li>\n" +
            "  </ul>\n" +
            "\n" +
            "  <p>We hope you like using our app and that it becomes an important part of your daily routine. Remember, your feedback is important to us, and we're constantly looking for ways to improve, so please share your ideas.</p>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"footer\">\n" +
            "  <p>Welcome aboard once more! Have fun connecting and exploring!</p>\n" +
            "  <p>Best regards,</p>\n" +
            "  <p>TiketiHub Support Team</p>\n" +
            "</div>\n" +
            "\n" +
            "</body>\n" +
            "</html>\n";
    private final String newEvent = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <title>Welcome to TiketiHub</title>\n" +
            "    <style>\n" +
            "        /* Add your CSS styles here to make the email look appealing */\n" +
            "        body {\n" +
            "            font-family: Arial, sans-serif;\n" +
            "            line-height: 1.6;\n" +
            "        }\n" +
            "\n" +
            "        .header {\n" +
            "            background-color: #f1f1f1;\n" +
            "            padding: 20px;\n" +
            "            text-align: center;\n" +
            "        }\n" +
            "\n" +
            "        .header h1 {\n" +
            "            color: #007bff;\n" +
            "        }\n" +
            "\n" +
            "        .content {\n" +
            "            padding: 20px;\n" +
            "        }\n" +
            "\n" +
            "        .cta-btn {\n" +
            "            display: inline-block;\n" +
            "            background-color: #007bff;\n" +
            "            color: #fff;\n" +
            "            padding: 10px 20px;\n" +
            "            text-decoration: none;\n" +
            "            border-radius: 5px;\n" +
            "            margin-top: 20px;\n" +
            "        }\n" +
            "\n" +
            "        .cta-btn:hover {\n" +
            "            background-color: #0056b3;\n" +
            "        }\n" +
            "\n" +
            "        .footer {\n" +
            "            background-color: #f1f1f1;\n" +
            "            padding: 10px;\n" +
            "            text-align: center;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "<div class=\"header\">\n" +
            "    <h1>You've Successfully Created an Event \uD83C\uDF89</h1>\n" +
            "    <p>Congratulations!</p>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"content\">\n" +
            "    <p>Your event has been successfully created on TiketiHub! We're thrilled to see your initiative and can't wait to see how it unfolds.</p>\n" +
            "\n" +
            "    <p><strong>Here are a few next steps:</strong></p>\n" +
            "\n" +
            "    <ul>\n" +
            "        <li>Share Your Event: Spread the word about your event. The more people know, the more exciting it will be!</li>\n" +
            "        <li>Stay Engaged: Keep an eye on your event page for interactions, questions, and updates from attendees.</li>\n" +
            "        <li>Prepare for the Big Day: Make sure everything is set for your event day, from venue arrangements to any materials you might need.</li>\n" +
            "        <li>Have a Blast: Enjoy the event you've put together and make it an unforgettable experience for all participants.</li>\n" +
            "    </ul>\n" +
            "\n" +
            "    <p>We're here to support you along the way. If you have any questions or need assistance, feel free to reach out to our support team.</p>\n" +
            "\n" +
            "    <a href=\"http://192.168.1.106:8080/TiketiHub/home\" class=\"cta-btn\">Visit TiketiHub</a>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"footer\">\n" +
            "    <p>Thank you for choosing TiketiHub to host your event. We wish you every success!</p>\n" +
            "    <p>Best regards,</p>\n" +
            "    <p>The TiketiHub Team</p>\n" +
            "</div>\n" +
            "</body>\n" +
            "\n" +
            "</html>\n";

}
