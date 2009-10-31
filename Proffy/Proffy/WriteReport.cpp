/*
    Copyright (C) 2009  Paul Richards.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
#include "stdafx.h"

#include "WriteReport.h"

#include "Assert.h"
#include "CommandLineArguments.h"
#include "Results.h"
#include "Utilities.h"
#include "XercesInitialize.h"

namespace Proffy {
    void WriteXmlReport(
        const CommandLineArguments* const arguments,
        const Results* const results)
    {
        XercesInitialize xerces;
        xercesc::DOMImplementation* const domImplementation =
            xercesc::DOMImplementationRegistry::getDOMImplementation(NULL);
        ASSERT(domImplementation != NULL);

        xercesc::DOMDocument* const document = domImplementation->createDocument();
        document->appendChild(document->createProcessingInstruction(L"xml-stylesheet", L"type=\"text/xsl\" href=\"Xhtml.xsl\""));
        xercesc::DOMElement* const root = document->createElement(L"ProffyResults");
        document->appendChild(root);

        {
            xercesc::DOMElement* const summary = document->createElement(L"Summary");

            xercesc::DOMElement* const delayBetweenSamplesInSeconds = document->createElement(L"DelayBetweenSamplesInSeconds");
            delayBetweenSamplesInSeconds->setTextContent(Utilities::ToWString(arguments->fDelayBetweenSamplesInSeconds).c_str());
            summary->appendChild(delayBetweenSamplesInSeconds);

            xercesc::DOMElement* const sampleCount = document->createElement(L"SampleCount");
            sampleCount->setTextContent(Utilities::ToWString(results->fNumberOfSamples).c_str());
            summary->appendChild(sampleCount);

            xercesc::DOMElement* const callstackCount = document->createElement(L"CallstackCount");
            callstackCount->setTextContent(Utilities::ToWString(results->fNumberOfCallstacks).c_str());
            summary->appendChild(callstackCount);

            xercesc::DOMElement* const wallClockTimeInSeconds = document->createElement(L"WallClockTimeInSeconds");
            wallClockTimeInSeconds->setTextContent(Utilities::ToWString(results->fEndTimeInSeconds - results->fBeginTimeInSeconds).c_str());
            summary->appendChild(wallClockTimeInSeconds);

            root->appendChild(summary);
        }

        std::set<std::wstring> sourceFiles;
        {
            xercesc::DOMElement* const pointsEncountered = document->createElement(L"PointsEncountered");

            for (std::set<PointInProgram>::const_iterator i = results->fEncounteredPoints.begin();
                i != results->fEncounteredPoints.end();
                ++i) {
                const int id = static_cast<int>(std::distance(results->fEncounteredPoints.begin(), i));

                sourceFiles.insert(i->fFileName);

                xercesc::DOMElement* const point = document->createElement(L"Point");
                point->setAttribute(L"Id",
                    Utilities::ToWString<int>(id).c_str());
                point->setAttribute(L"SymbolName",
                    i->fSymbolName.c_str());
                point->setAttribute(L"SymbolDisplacement",
                    Utilities::ToWString<int>(i->fSymbolDisplacement).c_str());
                point->setAttribute(L"FileName",
                    i->fFileName.c_str());
                point->setAttribute(L"LineNumber",
                    Utilities::ToWString<int>(i->fLineNumber).c_str());
                point->setAttribute(L"LineDisplacement",
                    Utilities::ToWString<int>(i->fLineDisplacement).c_str());

                pointsEncountered->appendChild(point);
            }

            root->appendChild(pointsEncountered);
        }

        {
            xercesc::DOMElement* const callCounters = document->createElement(L"CallCounters");

            for (std::map<std::pair<const PointInProgram*, const PointInProgram*>, int>::const_iterator i = results->fHits.begin();
                i != results->fHits.end();
                ++i) {
                    const PointInProgram* const caller = i->first.first;
                    const PointInProgram* const callee = i->first.second;
                    const int callerId =
                        static_cast<int>(std::distance(results->fEncounteredPoints.begin(),
                            results->fEncounteredPoints.find(*caller)));
                    const int calleeId = (callee == NULL) ? -1 :
                        static_cast<int>(std::distance(results->fEncounteredPoints.begin(),
                            results->fEncounteredPoints.find(*callee)));

                    xercesc::DOMElement* const counter = document->createElement(L"Counter");

                    counter->setAttribute(L"CallerId",
                        Utilities::ToWString<int>(callerId).c_str());
                    counter->setAttribute(L"CalleeId",
                        Utilities::ToWString<int>(calleeId).c_str());
                    counter->setAttribute(L"Count",
                        Utilities::ToWString<int>(i->second).c_str());

                    callCounters->appendChild(counter);
            }

            root->appendChild(callCounters);
        }

        {
            xercesc::DOMElement* const files = document->createElement(L"Files");

            for (std::set<std::wstring>::const_iterator i = sourceFiles.begin();
                i != sourceFiles.end();
                ++i) {

                const std::wstring filename = *i;
                xercesc::DOMElement* const file = document->createElement(L"File");
                file->setAttribute(L"Name", filename.c_str());

                std::wifstream fileStream(filename.c_str());
                if (fileStream.is_open()) {
                    for (int lineNumber = 1; fileStream.eof() == false; lineNumber++) {
                        std::wstring lineContents;
                        std::getline(fileStream, lineContents);

                        xercesc::DOMElement* const line = document->createElement(L"Line");
                        line->setAttribute(L"Number", Utilities::ToWString<int>(lineNumber).c_str());

                        line->appendChild(document->createCDATASection(lineContents.c_str()));
                        file->appendChild(line);
                    }
                }
                files->appendChild(file);
            }
            root->appendChild(files);
        }

        xercesc::DOMLSSerializer* const domSerializer = domImplementation->createLSSerializer();
        domSerializer->getDomConfig()->setParameter(xercesc::XMLUni::fgDOMWRTFormatPrettyPrint, true);
        xercesc::XMLFormatTarget* const target = new xercesc::LocalFileFormatTarget(arguments->fXmlOutputFilename.c_str());
        xercesc::DOMLSOutput* const output = domImplementation->createLSOutput();
        output->setByteStream(target);
        domSerializer->write(document, output);
    }

    namespace {
        template<typename T1, typename T2, typename T3> class triple
        {
        public:
            typedef T1 Type1;
            typedef T2 Type2;
            typedef T3 Type3;

            Type1 first;
            Type2 second;
            Type3 third;

            triple() :
                first(Type1()),
                second(Type2()),
                third(Type3())
            {
            }
        };
    }

    void WriteDotReport(
        const CommandLineArguments* const arguments,
        const Results* const results)
    {
        std::wofstream dotStream(arguments->fDotOutputFilename.c_str());


        dotStream << L"strict digraph Awesome {\n";

        std::map<std::wstring, triple<int, std::map<std::wstring, int>, int> > tally;

        // Ensure each symbol is present in the tally map.
        for (std::set<PointInProgram>::const_iterator i = results->fEncounteredPoints.begin();
            i != results->fEncounteredPoints.end();
            ++i) {
            tally[i->fSymbolName];
        }

        // Accumulate symbol to symbol call counters
        for (std::map<std::pair<const PointInProgram*, const PointInProgram*>, int>::const_iterator i = results->fHits.begin();
            i != results->fHits.end();
            ++i) {
            const PointInProgram* const caller = i->first.first;
            const PointInProgram* const callee = i->first.second;

            if (callee != NULL) {
                std::map<std::wstring, int>& tmp = tally.find(caller->fSymbolName)->second.second;
                tmp[callee->fSymbolName] += i->second;
            } else {
                tally.find(caller->fSymbolName)->second.third += i->second;
            }
        }

        // Give each symbol a unique id number and output nodes.
        int counter = 0;
        for (std::map<std::wstring, triple<int, std::map<std::wstring, int>, int > >::iterator i = tally.begin();
            i != tally.end();
            ++i) {
            i->second.first = (counter++);

            dotStream << L"    node_" << (i->second.first) << L" [label=\"" << (i->first) << L"\\n" << i->second.third << "\"];\n";
        }

        dotStream << L"\n";

        // Output edges.
        for (std::map<std::wstring, triple<int, std::map<std::wstring, int>, int  > >::const_iterator i = tally.begin();
            i != tally.end();
            ++i) {
            for (std::map<std::wstring, int>::const_iterator j = i->second.second.begin();
                j != i->second.second.end();
                ++j) {
                const int callerId = tally.find(i->first)->second.first;
                const int calleeId = tally.find(j->first)->second.first;
                const int callCount = tally.find(i->first)->second.second.find(j->first)->second;
                dotStream << L"    node_" << callerId << L" -> node_" << calleeId << L" [label=\"" << callCount << L"\", weight=" << callCount << L"];\n";
            }
        }

        dotStream << L"}\n";



        dotStream.close();
    }

    void WriteReport(
        const CommandLineArguments* const arguments,
        const Results* const results)
    {
        WriteXmlReport(arguments, results);
        WriteDotReport(arguments, results);
    }
}