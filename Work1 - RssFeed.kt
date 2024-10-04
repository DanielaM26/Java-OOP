package ro.andrei.ioja.tuiasi

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.Print

class Item(var title : String, var link : String, var description : String, var pubDate : String) {
    fun Print() {
        println("\nTitle: " + title)
        println("Link: " + link)
        //println("Description: " + description)
        //println("pubDate: " + pubDate)
    }
}

class FeedItems(var feed : MutableList<Item>) {
    fun AddItem(item : Item) {
        // Adaugare stire la Lista
        feed += item
    }
    fun Print() {
        println("Afisare lista RSS Feed")
        for(item in feed) {
            item.Print()
        }
    }
}

class FeedGenerator(val URL : String) {
    var htmlDocument: Document? = null
    fun initFeed() {
        // Conectare la website si extragere informatii
        try {
            htmlDocument = Jsoup.connect(URL).get()
        } catch(e : Exception) {
            println("EROARE: Conectare esuata!")
        }
    }
    fun getFeed() : FeedItems {
        // Creare ADT
        val news = FeedItems(mutableListOf<Item>())
        // Extragere stiri din feed
        val newsItems = (htmlDocument!!).getElementsByTag("item")
        for(newsItem in newsItems) {
            try {
                val title = newsItem.getElementsByTag("title").text()
                val link = newsItem.getElementsByTag("link").text()
                val description = newsItem.getElementsByTag("description").text()
                val pubDate = newsItem.getElementsByTag("pubDate").text()
                news.AddItem(Item(title, link, description, pubDate))
            } catch(e : Exception) {
                println("EROARE: Stire incompleta! (Nu a fost adaugata in feed)")
            }
        }
        return news
    }
}

fun main(args: Array<String>) {
    val feed : FeedGenerator = FeedGenerator("http://feeds.bbci.co.uk/news/rss.xml")
    feed.initFeed()
    val newsItems = feed.getFeed()
    newsItems.Print()
}

