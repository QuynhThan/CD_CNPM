# /*
#  * odd chances - naive bayes
#  * N19DCCN140-Trần Tấn Phong
#  * N19DCCN147-Lê Nguyễn Duy Phương
#  * N19DCCN154- Thân Ngọc Quỳnh
#  */
import re
from nltk.corpus import stopwords
import nltk
relevant_doc_query = {"id_query": "", "id_docs": ""}

f_rlv_ass = open("./npl/rlv-ass", "r")
test_list = []
text = ""
for line in f_rlv_ass:
    if line.strip() == "/":
        test_list.append(text)
        text = ""
        continue
    text += line.strip() + "\n"

test_list = [x[:-1] for x in test_list]

tmp = []
result_relevant_query_list = []

for x in test_list:
    for index, value in enumerate(x.split("\n")):
        if index == 0:
            relevant_doc_query["id_query"] = value
            continue
        relevant_doc_query["id_docs"] += value
    result_relevant_query_list.append(relevant_doc_query)
    relevant_doc_query = {"id_query": "", "id_docs": ""}

# print("testobj")
# print(result)
result_relevant_query_list = [
    {"id_query": int(x["id_query"]), "id_docs": x["id_docs"]}
    for x in result_relevant_query_list
]

# doc documents
doc = {
    "id": 1,
    "content": "",
}
list_docs = []
f_doc = open("./npl/doc-text", "r")

for x in f_doc:
    # x = x.strip()[:-1]
    # x = x.strip()[:-1]
    if x.strip().isnumeric():
        doc["id"] = int(x)
        continue
    elif x.strip() == "/":
        list_docs.append(doc)
        doc = {"id": 1, "content": ""}
    else:
        x = x[:-1]  # xoa '\n' cuoi dong
        doc["content"] += x

    # if (x.isnumeric()):
    #     list_test.append(x)

f_doc.close()

# load file truy van
query = {
    "id": 1,
    "content": "",
}
list_queries = []
f_query = open("./npl/query-text", "r")

for x in f_query:
    # x = x.strip()[:-1]
    # x = x.strip()[:-1]
    if x.strip().isnumeric():
        query["id"] = int(x)
        continue
    elif x.strip() == "/":
        list_queries.append(query)
        query = {"id": 1, "content": ""}
    else:
        x = x[:-1]  # xoa '\n' cuoi dong
        query["content"] += x.lower()


f_query.close()
# remove stop words in the query

# download the stop words if not already downloaded
nltk.download("stopwords")

# create a set of stop words
stop_words = set(stopwords.words("english"))

# example text
for query_obj in list_queries:
    text = query_obj["content"]
    # remove stop words from text
    filtered_text = [word for word in text.split() if word.lower()
                     not in stop_words]
    query_obj["content"] = " ".join(filtered_text)


# print(result_relevant_query_list)


def findWholeWord(w):
    return re.compile(r"\b({0})\b".format(w), flags=re.IGNORECASE).search


for query_obj in list_queries:
    query_obj["docs"] = []
    relevant_doc_query_item = {"id_query": "", "id_docs": ""}
    for rlv_query_obj in result_relevant_query_list:
        if rlv_query_obj["id_query"] == query_obj["id"]:
            relevant_doc_query_item["id_query"] = rlv_query_obj["id_query"]
            relevant_doc_query_item["id_docs"] = rlv_query_obj["id_docs"]
        else:
            continue
        for doc in list_docs:
            doc_obj = {"id_doc": "", "relevant": False}
            x = relevant_doc_query_item.get("id_docs")
            doc_id = str(doc["id"])
            doc_obj["id_doc"] = doc_id
            if findWholeWord(doc_id)(x):
                print("iq", relevant_doc_query_item["id_query"])
                print("idoc", doc_id)
                print("True")
                doc_obj["relevant"] = True
            else:
                print("iq", relevant_doc_query_item["id_query"])
                print("idoc", doc_id)
                print("False")
            doc_obj["id_doc"] = int(doc_obj["id_doc"])
            query_obj["docs"].append(doc_obj)
print("--------------------------------")


# Compute the Odds ratio for each term
# The Odds ratio measures the likelihood of a term being present in relevant documents
# versus its likelihood of being present in non-relevant documents
for query_obj in list_queries:
    odds_ratios = {}
    query_obj["content"] = [x.strip() for x in query_obj["content"].split(" ")]
    for term in query_obj["content"]:
        relevant_count = 0
        nonrelevant_count = 0
        for doc in list_docs:
            doc_id = doc["id"]
            # tim doc trong query_obj
            doc_query_obj = {}
            for temp in query_obj["docs"]:
                if temp["id_doc"] == doc_id:
                    doc_query_obj = temp
                    break
            if findWholeWord(term)(doc["content"]):
                if doc_query_obj["relevant"] == 1:
                    relevant_count += 1
                else:
                    nonrelevant_count += 1
        odds_ratios[term] = (relevant_count / (len(list_docs) - relevant_count)) / (
            nonrelevant_count / (len(list_docs) - nonrelevant_count)
        )
    query_obj["odds_ratios"] = odds_ratios
    break

print(list_queries[0])
# Compute the Naive Bayes probability for each document
# The Naive Bayes probability is the product of the conditional probabilities
# of the query terms given each document's label
for query_obj in list_queries:
    for doc in list_docs:
        prob_relevant = 1.0
        prob_notrelevant = 1.0
        for term in query_obj["content"]:
            if findWholeWord(term)(doc["content"]):
                prob_relevant *= query_obj["odds_ratios"][term] / (
                    1 + query_obj["odds_ratios"][term]
                )
                prob_notrelevant *= 1 - (
                    query_obj["odds_ratios"][term]
                    / (1 + query_obj["odds_ratios"][term])
                )
            else:
                prob_relevant *= 1 - query_obj["odds_ratios"][term] / (
                    1 + query_obj["odds_ratios"][term]
                )
                prob_notrelevant *= query_obj["odds_ratios"][term] / (
                    1 + query_obj["odds_ratios"][term]
                )
        prob_relevant *= 0.5  # prior probability of a document being relevant
        prob_notrelevant *= 0.5  # prior probability of a document being not relevant
        # doc["probability"] = prob_relevant / (prob_relevant + prob_notrelevant)
        for doc_temp_to_assign_probability in query_obj["docs"]:
            if doc["id"] == doc_temp_to_assign_probability["id_doc"]:
                doc_temp_to_assign_probability["probability"] = prob_relevant / (
                    prob_relevant + prob_notrelevant
                )
                break
    query_obj["docs"] = sorted(
        query_obj["docs"], key=lambda doc: doc["probability"], reverse=True
    )
    break
print(list_queries[0])
