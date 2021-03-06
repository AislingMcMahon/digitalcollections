package com.aisling.digitalcollections;

/**
 * Created by osulld13 on 08/02/16.
 * Adapted from the Android XML parsing documentation, http://developer.android.com/training/basics/network-ops/xml.html
 */

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ResponseXMLParser {

    private static final String ns = null;

    // parse search is passed the InputStream generated by a Solr Request and returns a list of Document
    // objects parsed from the List
    public List parseSearchResponse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readResponse(parser);
        } finally {
            in.close();
        }
    }

    private List readResponse(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = null;
        parser.require(XmlPullParser.START_TAG, ns, XMLParsingConstants.response_tag);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(XMLParsingConstants.result_tag)) {
                entries = readResult(parser);
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private List readResult(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, XMLParsingConstants.result_tag);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the document tag
            if (name.equals(XMLParsingConstants.document_tag)) {
                entries.add(readDoc(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    // Parses the contents of an Document. If it encounters a pid, drisFolderNumber, title or link genre attribute tags, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Document readDoc(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, XMLParsingConstants.document_tag);
        String pid = null;
        String drisFolderNumber = null;
        String genre = null;
        String allText = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            // Get first attribute of each tag
            String attributeValue = parser.getAttributeValue(0);
            if (attributeValue.equals(XMLParsingConstants.pid_attribute)) {
                pid = readPid(parser);
            } else if (attributeValue.equals(XMLParsingConstants.dris_folder_attribute)) {
                drisFolderNumber = readDrisFolder(parser);
            } else if (attributeValue.equals(XMLParsingConstants.genre_attribute)) {
                genre = readGenre(parser);
            } else if (attributeValue.equals(XMLParsingConstants.all_text_attribute)) {
                allText = readAllText(parser);
            } else {
                skip(parser);
            }
        }
        return new Document(pid, drisFolderNumber, genre, allText);
    }

    // Processes pids in the feed.
    private String readPid(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, XMLParsingConstants.string_tag);
        String pid = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, XMLParsingConstants.string_tag);
        return pid;
    }

    // Processes AllText in the feed.
    private String readAllText(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, XMLParsingConstants.array_tag);
        String allText = "";

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals(XMLParsingConstants.string_tag)){
                allText += readText(parser) + " ";
            } else {
                skip(parser);
            }
        }
        return allText;
    }

    // Processes drisFolders in the feed.
    private String readDrisFolder(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, XMLParsingConstants.string_tag);
        String drisFolder = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, XMLParsingConstants.string_tag);
        return drisFolder;
    }

    // Processes genres in the feed.
    private String readGenre(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, XMLParsingConstants.string_tag);
        String genre = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, XMLParsingConstants.string_tag);
        return genre;
    }

    // Extracts the text values of a tag.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Method to skip tags in the parser for when we are not interested in them
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}