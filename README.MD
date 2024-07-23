# Document extractor play project

This project is meant to illustrate some ideas around extracting information from LLMs using googles vertex ai api.

# Running the program

## check out code

This project is hosted on github and is available publicly

````git clone git@github.com:browndar/MedicalRecords.git````

## add service credentials

I've provided service credentials for my account with vertex. I'll disable them after a some time. Copy this file to 'src/main/resources'. It must have the same name as was provided (ending in json file extension as well)

## build

````./gradlew build````

## run

assuming no failures, you can simply run the main class

````./gradlew run````

this will run the program against the supplied example. The program also takes a single argument specifying the document to process

````./gradlew run --args="ChartNotes.txt"````

In this case ChartNotes.txt is a sibling file of build.gradle.kts at the root of the repository

# Some notes

## My approach

My approach was to stream the document until I reach a threshold of tokens. Then I process the document thus far, keeping track of a running summary a list of key entities, and a running outline. After I have run through the entire document:

* I finalize the summary with a predict call, 
* I write out the entities that have been discovered, 
* I collect the outline
* I generate a title from the summary using predict text

I then write all of these out to a serializable class and use the json library to produce the output which is logged. 

All of these tasks are done with prompts of the form of

````
DOCUMENT

Q: this is a training prompt
A: I answer my own training prompt

Q: this is the predict text prompt?
A:
````

* DOCUMENT is the text that I want to summarize in some way with the model
* I can guide training by asking an answering my own questions
* Finally I can ask a quesiton of the model. 
* The prompts can all by found in the constant class

Here is the example output for the provided document:

````
{
 "title": "Jane Doe - Patient Report",
 "entities": {
  "name": "Jane Doe",
  "date of birth": "02/02/1878",
  "location": "3800 West Slaughter Lane, Austin, TX 78000"
 },
 "summary": "Jane Doe is a 42-year-old female who presented to the office today for complaint(s) resulting from an automobile vs. automobile incident. She presented for treatment today, complaining of frequent (50%-75%) sharp pain, shooting and diffusediscomfort in the back of the neck, upper back, chest, mid back, low back, right shoulder both hips, right leg andfrequent headaches. She rated the intensity of discomfort, using a VAS, as a level 6 on a scale of 1 to 10 with10 being the most severe. The discomfort was reported to increase with movement, applied pressure,prolonged sitting and coughing/sneezing.",
 "outline": {
  "history of present illness": [
   "Jane Doe is a 42-year-old female who presented to the office today for complaint(s) resulting from an automobile vs. automobile incident having completed the patient intake questionnaire. The questionnaire was reviewed and annotated by the examining provider as needed. The completed questionnaire is in the patient's permanent paper file and available for review. She signed consent for evaluation and possible treatment of injuries sustained as a direct result of the accident that occurred on or about 12/17/2020."
  ],
  "Objective": [
   "Spinal Restriction(s)/Subluxation(s): occiput, C1, right C2, right C3, right C4, right C5, right C6, right C7, right T1, right T2, T3, T4, T5, L1, L2, T12, right pelvis, right sacrum, right L5, right L4 and right L3",
   "Extraspinal Restrictions/Subluxations: right shoulder and right hip",
   "Pain/Tenderness: upper to mid cervical, mid to lower cervical, cervico-thoracic, thoraco-lumbar, upperlumbar, lower lumbar and shoulder",
   "Postural Analysis: high right shoulder and head rotation right",
   "Muscle Spasm(s): moderate muscle spasms in the following areas; left cervical, right cervical, right cervicaldorsal, upper thoracic, right posterior shoulder, right mid thoracic, mid thoracic, right lower thoracic, rightlumbar, lumbar, left sacroiliac, sacral, right sacroiliac, right pelvic, right buttock, right posterior leg and right hip",
   "ROM Concern(s): entire lumbar and cervical spine, thoracic flexion and thoracic extension was recorded asmoderately reduced with pain noted",
   "Pelvis: right sacroiliac joint was noted to be restricted",
   "Gait: antalgic gait was noted",
   "Neurologic: negative straight leg raise bilaterally",
   "Reflexes: 2+ bilaterally",
   "Sensation: intact bilaterally",
   "Range of Motion: cervical flexion 45 degrees, extension 45 degrees, right rotation 45 degrees, left rotation 45 degrees, right lateral bending 45 degrees, left lateral bending 45 degrees, thoracic flexion 45 degrees, extension 45 degrees, right rotation 45 degrees, left rotation 45 degrees, right lateral bending 45 degrees, left lateral bending 45 degrees, lumbar flexion 45 degrees, extension 45 degrees, right rotation 45 degrees, left rotation 45 degrees, right lateral bending 45 degrees, left lateral bending 45 degrees",
   "Special Tests: Spurling's test was negative bilaterally"
  ],
  "Complaints": [
   "back of the neck",
   "upper back",
   "chest",
   "mid back",
   "low back",
   "right shoulder",
   "both hips",
   "right leg",
   "frequent headaches"
  ],
  "Treatment": [
   "Myofascial Release was applied to the right shoulder and right hip",
   "Electrical stimulation in the form of pre-modulated current was applied to cervical dorsal, shoulder and thoracic",
   "Moist heat was applied to the right shoulder and right hip",
   "Mechanical traction was implemented to the right shoulder and right hip"
  ],
  "chief complaint": [
   "frequent (50%-75%) sharp pain, shooting and diffusediscomfort in the back of the neck, upper back, chest, mid back, low back, right shoulder both hips, right leg andfrequent headaches"
  ]
 }
}
````

## Some comments about my approach

* Completely automated extraction. I decided to mostly allow genai to produce the entire extraction. If I was doing document extraction in production I would spend a fair amount of time learning about the documents I was going to work against and if there were any hard rules that I could work off of. I would work first to take the structure out that I know about and then use genai to help woth some of the processes that I've had good luck with such as statistics and metrics extraction or summarization. For example, with the provided document I might split the document by the "Chart Notes" separator. But I decided to just stream the document until I got to a defined limit on tokens.
* Allowing genai to provide json. I specify format as part of the prompt. There are two places where I do this to get structured json extracted from the document. In production this might be brittle and I would do some work validating the return and failing back to other forms of extraction if the model failed to produce desired results. Here if the extraction fails I just leave it blank.
* Vertex auth. If I were hosting a solution I would have better options on using the vertex api without supplying service creds but this seemed like a viable solution for having conversations around prompt engineering and such. But I'm assuming that this sort of infrastructure considerations are not incredibly important to our current discussions.
* Prompt limits. Usually prompts have to be something like 4K tokens or less. There are certainly edge cases around summarizing large docs. My overall summary would overcome that limit with larger documents. 
* Rolling summary. For sufficiently large documents you can't just throw one prompt at a model to get a summary. I illustrated a couple of approached, such as building a summary as you iterate through the document as I do with the actual summary field, or looking to extract what I can as I iterate through the document as I do with the entities data structure. These are illustrations.
* Config. I have a bunch of constants that I use to build up the model and test. It would make more sense to have these be in some parsed configuration file depending on the platform (application.yaml for spring for example)
* Caching. Since I was testing a fair amount I put together a quick caching mecanism to help speed up calls that I have recently made. Not much value when actually looking to extract documents. 

## Learning some new tricks

I wanted to take some time to learn some new things if I was going to take the time to run through the exercise. 

* Windows. I installed everything from scractch on my surface pro. I have been using a mac for several decades and was curious what the experience would be like away from that ecosystem. Turns out it's fairly painful! Lots of little hiccups. Getting more comfy but it definitely has been a bit of a slog for me to develop away from mac.
* Google vertex ai. I had some different choices to look at. I pay for gemini so I figured I'd look into vertex. I had played around with chatgpt and just a little in the hugging face infrastructure to deploy but hadn't really used an api that much for prompts. 
* Kotlin. I have been looking into some different tech stacks with some of my free time and used this as an excuse to give kotlin a look. So far I am quite impressed. 


