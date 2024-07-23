# Document extractor play project

This project is meant to illustrate some ideas around extracting information from LLMs using googles vertex ai api.

# Running the program

## check out code

This project is hosted on github and is available publicly

git clone git@github.com:browndar/MedicalRecords.git

## add service credentials

I've provided service credentials for my account with vertex. I'll disable them after a some time. Copy this file to 'src/main/resources'. It must have the same name as was provided (ending in json file extension as well)

## build

./gradlew build

## run

assuming no failures, you can simply run the main class

./gradlew run

# Some notes

## My approach

My approach was to stream the document until I reach a threshold of tokens. Then I process the docuemnt thus far, keeping track of a running summary and a list of key entities. After I have run through the entire document:

* I finalize the summary with a predict call, 
* I write out the entities that have been discovered, 
* I generate a title from the summary using predict text
* I generate an outline from the summary using predict text

All of these tasks are done with prompts of the form

````
verbatim
````

* I decided to mostly allow genai to produce the entire extraction. If I was doing document extraction in production I would spend a fair amount of time learning about the documents I was going to work against and if there were any hard rules that I could work off of. I would Work first to take the structure out that I know about and then use genai to help woth some of the processes that I've had good luck with such as statistics and metrics extraction or summarization. For example, with the provided document I might split the document by the "Chart Notes" separator. But I decided to just stream the document until I got to a defined limit on tokens.
* and then 1) Keep a running summary of the document and then combine that summary with the current promot to form a running summary of the document

## Some assumptions

* Vertex auth. If I were hosting a solution I would have better options on using the vertex api without supplying service creds but this seemed like a viable solution for having conversations around prompt engineering and such. But I'm assuming that this sort of infrastructure considerations are not incredibly important to our current discussions.
* Prompt limits. Usually prompts have to be something like 4K tokens or less. There are certainly edge cases around summarizing the doc 

## Learning some new tricks

I wanted to take some time to learn some new things if I was going to take the time to run through the exercise. 

* Windows. I installed everything from scractch on my surface pro. I have been using a mac for several decades and was curious what the experience would be like away from that ecosystem. Turns out it's fairly painful! Lots of little hiccups. Getting more comfy but it definitely has been a bit of a slog for me to develop away from mac.
* Google vertex ai. I had some different choices to look at. I pay for gemini so I figured I'd look into vertex. I had played around with chatgpt and just a little in the hugging face infrastructure to deploy but hadn't really used an api that much for prompts. 
* Kotlin. I have been looking into some different tech stacks with some of my free time and used this as an excuse to give kotlin a look. So far I am quite impressed. 


