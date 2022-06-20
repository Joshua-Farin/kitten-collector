//#full-example
package com.example

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike
import com.example.Notifier.Notification
import com.example.KittenShipper.KittenShipment
import com.example.KittenProcessor.Kitten

//#definition
class AkkaQuickstartSpec
    extends ScalaTestWithActorTestKit
    with AnyWordSpecLike {
//#definition

  "A KittenShipper" must {

    "vaccinate batch of 1 cat" in {
      val replyProbe = createTestProbe[Notification]()
      val underTest = spawn(KittenShipper())
      val unvax = Kitten(id = 0, name = "Acorn", vaccinated = false)
      val expectedKittens = Map(unvax.id -> unvax)
      underTest ! KittenShipment(kitten = unvax, replyTo = replyProbe.ref)
      replyProbe.expectMessage(Notification(expectedKittens))
    }

    "no cats to vaccinate" in {
      val replyProbe = createTestProbe[Notification]()
      val underTest = spawn(KittenShipper())
      val unvax = Kitten(id = 1, name = "Tuna", vaccinated = false)
      val expectedKittens = Map(unvax.id -> unvax)
      underTest ! KittenShipment(kitten = unvax, replyTo = replyProbe.ref)
      replyProbe.expectMessage(Notification(expectedKittens))
    }
  }
}
//#full-example
