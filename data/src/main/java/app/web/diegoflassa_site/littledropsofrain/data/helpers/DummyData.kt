package app.web.diegoflassa_site.littledropsofrain.data.helpers

import android.net.Uri
import app.web.diegoflassa_site.littledropsofrain.data.entities.CategoryItem
import app.web.diegoflassa_site.littledropsofrain.data.entities.Product
import app.web.diegoflassa_site.littledropsofrain.data.entities.Source

class DummyData {
    companion object{
        fun getNewCollectionCarouselItems() : List<Product>{
            val ret= mutableListOf<Product>()
            ret.add(Product("", "0", "0", "", "Test A", 0,true,"true","","","http://s3.amazonaws.com/img.iluria.com/product/7021A8/1160FCB/450xN.jpg",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria A", "Categoria B")))
            ret.add(Product("", "0", "0", "", "Test B", 0,true,"true","","","http://s3.amazonaws.com/img.iluria.com/product/7B64A7/1648107/450xN.jpg",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria C", "Categoria D")))
            ret.add(Product("", "0", "0", "", "Test C", 0,true,"true","","","http://s3.amazonaws.com/img.iluria.com/product/7E9359/13B4AA1/450xN.jpg",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria E", "Categoria F")))
            return ret
        }

        fun getRecommendationsCarouselItems() : List<Product>{
            val ret= mutableListOf<Product>()
            ret.add(Product("", "0", "0", "", "Test A", 0,true,"true","","","http://s3.amazonaws.com/img.iluria.com/product/7E935A/13B4AA5/450xN.jpg",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria A", "Categoria B")))
            ret.add(Product("", "0", "0", "", "Test B", 0,true,"true","","","http://s3.amazonaws.com/img.iluria.com/product/7E9361/13B4AB7/450xN.jpg",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria C", "Categoria D")))
            ret.add(Product("", "0", "0", "", "Test C", 0,true,"true","","","http://s3.amazonaws.com/img.iluria.com/product/7E936C/13B4AD1/450xN.jpg",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria E", "Categoria F")))
            return ret
        }

        fun getSpotlightCarouselItems() : List<Product>{
            val ret= mutableListOf<Product>()
            ret.add(Product("", "0", "0", "", "Test A", 0,true,"true","","","http://s3.amazonaws.com/img.iluria.com/product/7E939E/13B4B87/450xN.jpg",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria A", "Categoria B")))
            ret.add(Product("", "0", "0", "", "Test B", 0,true,"true","","","http://s3.amazonaws.com/img.iluria.com/product/7E93C6/13B4BC9/450xN.jpg",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria C", "Categoria D")))
            ret.add(Product("", "0", "0", "", "Test C", 0,true,"true","","","http://s3.amazonaws.com/img.iluria.com/product/7E93DA/13B4BF2/450xN.jpg",mutableListOf("11"), Source.ILURIA.name, mutableListOf("Categoria E", "Categoria F")))
            return ret
        }

        fun getCategoriesCarouselItems() : List<CategoryItem>{
            val ret= mutableListOf<CategoryItem>()
            ret.add(CategoryItem(Uri.parse("http://s3.amazonaws.com/img.iluria.com/product/80839A/140A50C/450xN.jpg"), "Category A"))
            ret.add(CategoryItem(Uri.parse("http://s3.amazonaws.com/img.iluria.com/product/8083A2/140A528/450xN.jpg"), "Category B"))
            ret.add(CategoryItem(Uri.parse("http://s3.amazonaws.com/img.iluria.com/product/8083A5/140A532/450xN.jpg"), "Category C"))
            return ret
        }
    }
}