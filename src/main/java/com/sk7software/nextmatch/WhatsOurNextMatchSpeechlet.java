/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sk7software.nextmatch;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.*;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;

import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.util.json.JSONObject;
import com.sk7software.nextmatch.model.Match;
import com.sk7software.nextmatch.model.Position;
import com.sk7software.nextmatch.model.Result;
import com.sk7software.nextmatch.util.DateUtil;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andrew
 */
public class WhatsOurNextMatchSpeechlet implements SpeechletV2 {
    private static final Logger log = LoggerFactory.getLogger(WhatsOurNextMatchSpeechlet.class);

    private static final String FIXTURES_URL = "http://www.sk7software.co.uk/Fixtures/fixtures.php";
    private static final String RESULTS_URL = "http://www.sk7software.co.uk/Fixtures/results.php";
    private static final String LEAGUE_URL = "http://www.sk7software.co.uk/Fixtures/league.php";
    private static final String OUR_TEAM = "BRAMHALL";

    private List<Match> matches;
    private List<Result> results;
    private List<Position> league;
    private int indexSoFar = 0;

    @Override
    public void onSessionStarted(final SpeechletRequestEnvelope<SessionStartedRequest> speechletRequestEnvelope) {
        log.info("onSessionStarted requestId={}, sessionId={}",
                speechletRequestEnvelope.getRequest().getRequestId(),
                speechletRequestEnvelope.getSession().getSessionId());
    }

    @Override
    public SpeechletResponse onLaunch(final SpeechletRequestEnvelope<LaunchRequest> speechletRequestEnvelope) {
        log.info("onLaunch requestId={}, sessionId={}",
                speechletRequestEnvelope.getRequest().getRequestId(),
                speechletRequestEnvelope.getSession().getSessionId());
        return getNextMatchResponse();
    }

    @Override
    public SpeechletResponse onIntent(final SpeechletRequestEnvelope<IntentRequest> speechletRequestEnvelope) {
        IntentRequest request = speechletRequestEnvelope.getRequest();
        Session session = speechletRequestEnvelope.getSession();
        log.info("onIntent requestId={}, sessionId={}, intentName={}", request.getRequestId(),
                session.getSessionId(), (request.getIntent() != null ? request.getIntent().getName() : "null"));

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : "Invalid";

        switch (intentName) {
            case "NextMatchIntent":
                return getNextMatchResponse();
            case "MatchAfterIntent":
                return getMatchAfterResponse();
            case "LeagueTableIntent":
                return getLeagueTableResponse(false);
            case "LeaguePositionIntent":
                return getLeagueTableResponse(true);
            case "MatchDateIntent":
                return getMatchOnDateResponse(intent);
            case "LastResultIntent":
                return getResultResponse(1);
            case "LastXResultsIntent":
                int numResults = Integer.parseInt(intent.getSlot("number").getValue());
                return getResultResponse(numResults);
            case "AMAZON.HelpIntent":
                return getHelpResponse();
            case "AMAZON.StopIntent":
                return getStopResponse();
            default:
                return null; // TODO: return error
        }
    }

    @Override
    public void onSessionEnded(final SpeechletRequestEnvelope<SessionEndedRequest> speechletRequestEnvelope) {
        log.info("onSessionEnded requestId={}, sessionId={}",
                speechletRequestEnvelope.getRequest().getRequestId(),
                speechletRequestEnvelope.getSession().getSessionId());
    }


    private SpeechletResponse getNextMatchResponse() {
        StringBuilder speechText = new StringBuilder();
        try {
            String url = FIXTURES_URL;
            String matchesStr = getJsonResponse(url);
            matches = Match.createFromJSON(new JSONObject(matchesStr));

            if (matches.size() > 0) {
                indexSoFar = 0;
                Match m = matches.get(0);
                speechText.append("The next match is ");
                speechText.append(DateUtil.getDayDescription(m.getDate(), true));
                speechText.append(", against ");
                speechText.append(m.getOpponent(OUR_TEAM));
                speechText.append(", kicking-off at ");
                speechText.append(DateUtil.getTimeDescription(m.getDate()));
                speechText.append(" at ");
                speechText.append(m.getVenue());
            } else {
                speechText.append("Sorry, I couldn't find any upcoming matches");
            }
        } catch (Exception e) {
            speechText.append("Sorry, there was a problem finding your match dates");
            log.error(e.getMessage());
        }

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText.toString());

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Anything else?");
        reprompt.setOutputSpeech(repromptSpeech);

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("When's Our Next Match?");
        card.setContent(speechText.toString());
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse getMatchAfterResponse() {
        StringBuilder speechText = new StringBuilder();
        indexSoFar++;

        if (matches.size() > indexSoFar) {
            Match m = matches.get(indexSoFar);
            speechText.append("The match after that is ");
            speechText.append(DateUtil.getDayDescription(m.getDate(), false));
            speechText.append(", against ");
            speechText.append(m.getOpponent(OUR_TEAM));
            speechText.append(", kicking-off at ");
            speechText.append(DateUtil.getTimeDescription(m.getDate()));
            speechText.append(" at ");
            speechText.append(m.getVenue());
        } else {
            speechText.append("There are no more matches scheduled");
        }

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText.toString());

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Anything else?");
        reprompt.setOutputSpeech(repromptSpeech);

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("When's Our Next Match?");
        card.setContent(speechText.toString());
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse getMatchOnDateResponse(Intent intent) {
        StringBuilder speechText = new StringBuilder();
        String dateStr = intent.getSlot("matchDate").getValue();
        log.info("matchDate slot value: " + dateStr);
        DateTime matchDate = new DateTime(dateStr);

        List<Match> matchesOnDate = getMatchesOnDate(matches, matchDate);

        if (matchesOnDate.size() > 0) {
            boolean firstMatch = true;
            speechText.append("On ");
            speechText.append(DateUtil.getSpokenDate(matchDate));
            speechText.append(", there is a match against ");
            for (Match m : matchesOnDate) {
                if (firstMatch) {
                    firstMatch = false;
                } else {
                    speechText.append(", and one against ");
                }
                speechText.append(m.getOpponent(OUR_TEAM));
                speechText.append(", kicking-off at ");
                speechText.append(DateUtil.getTimeDescription(m.getDate()));
                speechText.append(" at ");
                speechText.append(m.getVenue());
            }
        } else {
            speechText.append("There are no matches scheduled on ");
            speechText.append(DateUtil.getSpokenDate(matchDate));
        }

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText.toString());

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Anything else?");
        reprompt.setOutputSpeech(repromptSpeech);

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("When's Our Next Match?");
        card.setContent(speechText.toString());
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse getResultResponse(int numResults) {
        StringBuilder speechText = new StringBuilder();
        int resultIndex = 0;

        try {
            if (results == null) {
                String url = RESULTS_URL;
                String resultsStr = getJsonResponse(url);
                results = Result.createFromJSON(new JSONObject(resultsStr));
            }

            if (results.size() > 0) {
                for (Result r : results) {
                    if (resultIndex < numResults) {
                        speechText.append(DateUtil.getSpokenDate(r.getDate()));
                        speechText.append(", ");
                        speechText.append(r.getTeam1());
                        speechText.append(", ");
                        speechText.append(r.getScore1());
                        speechText.append(", ");
                        speechText.append(r.getTeam2());
                        speechText.append(", ");
                        speechText.append(r.getScore2());
                        speechText.append(". ");
                        resultIndex++;
                    }
                }
            } else {
                speechText.append("Sorry, I couldn't find any results");
            }
        } catch (Exception e) {
            speechText.append("Sorry, there was a problem finding your results");
            log.error(e.getMessage());
        }

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText.toString());

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Anything else?");
        reprompt.setOutputSpeech(repromptSpeech);

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("When's Our Next Match?");
        card.setContent(speechText.toString());
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse getLeagueTableResponse(boolean position) {
        StringBuilder speechText = new StringBuilder();

        try {
            if (league == null) {
                String url = LEAGUE_URL;
                String leagueStr = getJsonResponse(url);
                league = Position.createFromJSON(new JSONObject(leagueStr));
            }

            if (league.size() > 0) {
                for (Position p : league) {
                    if (position && p.getTeam().toUpperCase().indexOf(OUR_TEAM) < 0) {
                        continue;
                    } else if (position) {
                        speechText.append("You are at position ");
                    }
                    speechText.append(p.getSpokenText());
                    speechText.append(". ");
                }
            } else {
                speechText.append("Sorry, I couldn't find any teams in the league");
            }
        } catch (Exception e) {
            speechText.append("Sorry, I couldn't fetch the league table");
        }

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText.toString());

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Anything else?");
        reprompt.setOutputSpeech(repromptSpeech);

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("When's Our Next Match?");
        card.setContent(speechText.toString());
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse getBinColourResponse(Intent intent) {
        StringBuilder speechText = new StringBuilder();

        if (matches != null && matches.size() > 0) {
            Slot slot = intent.getSlot("colour");
            if (slot != null && slot.getValue() != null) {
                String binColour = slot.getValue();

                for (Match b : matches) {
//                    if (binColour.equalsIgnoreCase(b.getColour())) {
//                        speechText.append("Your ");
//                        speechText.append(binColour);
//                        speechText.append(" bin will next be collected ");
//                        speechText.append(DateUtil.getDayDescription(b.getDate()));
//                    }
                }
            } else {
                speechText.append("Sorry I did not recognise the bin you requested.");
            }
        } else {
            speechText.append("Sorry I don't have any information about your matches.");
        }

        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText.toString());

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Anything else?");
        reprompt.setOutputSpeech(repromptSpeech);

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("When's My Bin Collection?");
        card.setContent(speechText.toString());
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    /*
    private SpeechletResponse getEchoAddressResponse(SpeechletRequestEnvelope<IntentRequest> speechletRequestEnvelope) {
        StringBuilder speechText = new StringBuilder("<speak>");
        EchoAddress echoAddress = null;
        CustomerAddress customerAddress;

        try {
            echoAddress = fetchEchoAddress(speechletRequestEnvelope);
            customerAddress = customerAddressDao.getAddress(speechletRequestEnvelope.getSession());

            speechText.append("The address I have for you is: ");
            speechText.append("<say-as interpret-as=\"address\">");
            speechText.append(echoAddress.getSpokenAddress());
            speechText.append("</say-as>");
            speechText.append(". I have matched this to: ");
            speechText.append("<say-as interpret-as=\"address\">");
            speechText.append(customerAddress.getAddress());
            speechText.append("</say-as>");
        } catch (UnauthorizedException ue) {
            return getUnauthorizedExceptionResponse();
        } catch (DeviceAddressClientException e) {
            speechText.append("Sorry, there was a problem finding your address");
            log.error(e.getMessage());
        }
        speechText.append("</speak>");

        SsmlOutputSpeech speech = new SsmlOutputSpeech();
        speech.setSsml(speechText.toString());

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("Anything else?");
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(speech, reprompt);
    }
    */

    private SpeechletResponse errorResponse(String message) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(message);
        SpeechletResponse response = new SpeechletResponse();
        response.setOutputSpeech(speech);
        response.setShouldEndSession(true);
        return response;
    }

    public static String getJsonResponse(String requestURL) {
        InputStreamReader inputStream = null;
        BufferedReader bufferedReader = null;
        String text;
        try {
            String line;
            URL url = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // set up url connection to get retrieve information back
            con.setRequestMethod("GET");

            inputStream = new InputStreamReader(con.getInputStream(), Charset.forName("US-ASCII"));
            bufferedReader = new BufferedReader(inputStream);
            StringBuilder builder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            text = builder.toString();
        } catch (IOException e) {
            // reset text variable to a blank string
            log.error(e.getMessage());
            text = "";
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(bufferedReader);
        }

        return text;
    }

    private DateTime getNextMatchDate(List<Match> matchList) {
        DateTime min = new DateTime(9999, 12, 31, 0, 0);

        for (Match b : matchList) {
            if (b.getDate().isBefore(min)) {
                min = b.getDate();
            }
        }

        return min;
    }

    private List<Match> getMatchesOnDate(List<Match> matchList, DateTime date) {
        List<Match> matchesOnDate = new ArrayList<>();

        for (Match m : matchList) {
            if (m.isMatchOnDate(date)) {
                matchesOnDate.add(m);
            }
        }

        return matchesOnDate;
    }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelpResponse() {
        StringBuilder helpText = new StringBuilder();
        helpText.append("You can ask when's my bin collection to get the date of ");
        helpText.append("your next collection and the matches that will be collected. ");
        helpText.append("You can ask when a particular colour bin will next be collected by saying, ");
        helpText.append("for example, when will my blue bin be collected. ");

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(helpText.toString());

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
        repromptSpeech.setText("");
        reprompt.setOutputSpeech(repromptSpeech);

        return SpeechletResponse.newAskResponse(speech, reprompt);
    }

    private SpeechletResponse getStopResponse() {
        String stopText = "Goodbye.";

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(stopText);

        SpeechletResponse stopResponse = new SpeechletResponse();
        stopResponse.setShouldEndSession(true);
        stopResponse.setOutputSpeech(speech);
        
        return stopResponse;
    }

}
